package com.example.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

data class AppUpdateInfo(
    val versionName: String,
    val tagName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val apkFileName: String,
    val apkSizeBytes: Long,
    val htmlUrl: String
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(val info: AppUpdateInfo) : UpdateCheckResult()
    data class UpToDate(val currentVersion: String) : UpdateCheckResult()
    data class Error(val message: String) : UpdateCheckResult()
}

sealed class InstallLaunchResult {
    data object LaunchedInstaller : InstallLaunchResult()
    data class NeedsUnknownSourcesPermission(val settingsIntent: Intent) : InstallLaunchResult()
    data class Failed(val error: String) : InstallLaunchResult()
}

class AppUpdaterManager(private val context: Context) {

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    suspend fun checkForUpdates(currentVersionName: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(LATEST_RELEASE_API)
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "BrainPulse-Android-Updater")
                .header("Cache-Control", "no-cache")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext UpdateCheckResult.Error("HTTP ${response.code}")
                }
                val bodyString = response.body?.string()
                    ?: return@withContext UpdateCheckResult.Error("Empty response")

                val json = JSONObject(bodyString)
                val tagName = json.optString("tag_name", "").trim()
                val releaseName = json.optString("name", tagName).trim().ifEmpty { tagName }
                val releaseNotes = json.optString("body", "").trim()
                val htmlUrl = json.optString("html_url", RELEASES_LATEST_URL).ifEmpty { RELEASES_LATEST_URL }

                val assets = json.optJSONArray("assets")
                var apkUrl = ""
                var apkName = ""
                var apkSize = 0L

                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.optJSONObject(i) ?: continue
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            apkUrl = asset.optString("browser_download_url", "")
                            apkName = name
                            apkSize = asset.optLong("size", 0L)
                            if (name.startsWith("Brain.", ignoreCase = true)) {
                                break
                            }
                        }
                    }
                }

                val remoteVersion = extractCleanVersion(tagName, apkName)
                if (remoteVersion.isEmpty()) {
                    return@withContext UpdateCheckResult.Error("Invalid release tag")
                }

                if (isRemoteVersionNewer(currentVersionName, remoteVersion)) {
                    val info = AppUpdateInfo(
                        versionName = remoteVersion,
                        tagName = tagName.ifEmpty { "v$remoteVersion" },
                        releaseTitle = releaseName,
                        releaseNotes = releaseNotes,
                        apkDownloadUrl = apkUrl,
                        apkFileName = apkName.ifEmpty { "Brain.v$remoteVersion.apk" },
                        apkSizeBytes = apkSize,
                        htmlUrl = htmlUrl
                    )
                    UpdateCheckResult.UpdateAvailable(info)
                } else {
                    UpdateCheckResult.UpToDate(currentVersionName)
                }
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun downloadApk(
        updateInfo: AppUpdateInfo,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val downloadUrl = updateInfo.apkDownloadUrl
            if (downloadUrl.isBlank()) {
                return@withContext Result.failure(IllegalStateException("APK asset URL missing"))
            }

            val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
            // Clean previous cached APKs to preserve device storage
            updatesDir.listFiles()?.forEach { file ->
                if (file.isFile) file.delete()
            }

            val finalFile = File(updatesDir, updateInfo.apkFileName)
            val tempFile = File(updatesDir, "${updateInfo.apkFileName}.tmp")

            val request = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "BrainPulse-Android-Updater")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IllegalStateException("Download failed: HTTP ${response.code}"))
                }

                val body = response.body
                    ?: return@withContext Result.failure(IllegalStateException("Empty APK download body"))

                val contentLength = body.contentLength().takeIf { it > 0 } ?: updateInfo.apkSizeBytes
                var totalRead = 0L
                var lastProgress = -1

                body.byteStream().use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            totalRead += read
                            if (contentLength > 0) {
                                val pct = ((totalRead * 100L) / contentLength).toInt().coerceIn(0, 100)
                                if (pct != lastProgress) {
                                    lastProgress = pct
                                    withContext(Dispatchers.Main) {
                                        onProgress(pct)
                                    }
                                }
                            }
                        }
                        output.flush()
                    }
                }

                if (finalFile.exists()) finalFile.delete()
                if (!tempFile.renameTo(finalFile)) {
                    tempFile.copyTo(finalFile, overwrite = true)
                    tempFile.delete()
                }
                withContext(Dispatchers.Main) {
                    onProgress(100)
                }
                Result.success(finalFile)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun canRequestPackageInstalls(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun createUnknownSourcesPermissionIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun launchApkInstaller(apkFile: File): InstallLaunchResult {
        return try {
            if (!apkFile.exists()) {
                return InstallLaunchResult.Failed("APK file not found")
            }
            if (!canRequestPackageInstalls()) {
                return InstallLaunchResult.NeedsUnknownSourcesPermission(createUnknownSourcesPermissionIntent())
            }
            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
            InstallLaunchResult.LaunchedInstaller
        } catch (e: Exception) {
            InstallLaunchResult.Failed(e.localizedMessage ?: "Failed to start installer")
        }
    }

    fun createReleasesBrowserIntent(url: String = RELEASES_LATEST_URL): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    companion object {
        const val REPO_URL = "https://github.com/charfeddine23/Brain"
        const val RELEASES_LATEST_URL = "https://github.com/charfeddine23/Brain/releases/latest"
        const val LATEST_RELEASE_API = "https://api.github.com/repos/charfeddine23/Brain/releases/latest"

        fun extractCleanVersion(tagName: String, assetName: String = ""): String {
            val fromTag = tagName.trim()
                .removePrefix("v")
                .removePrefix("V")
                .substringBefore("-")
                .trim()
            if (fromTag.isNotEmpty() && fromTag.first().isDigit()) {
                return fromTag
            }
            val regex = Regex("""v?(\d+\.\d+(?:\.\d+)*)""")
            return regex.find(assetName)?.groupValues?.getOrNull(1) ?: ""
        }

        fun isRemoteVersionNewer(currentVersion: String, remoteVersion: String): Boolean {
            val currentParts = parseVersionParts(currentVersion)
            val remoteParts = parseVersionParts(remoteVersion)
            val maxLen = maxOf(currentParts.size, remoteParts.size)
            for (i in 0 until maxLen) {
                val c = currentParts.getOrElse(i) { 0 }
                val r = remoteParts.getOrElse(i) { 0 }
                if (r > c) return true
                if (r < c) return false
            }
            return false
        }

        private fun parseVersionParts(version: String): List<Int> {
            val cleaned = version.trim()
                .removePrefix("v")
                .removePrefix("V")
                .substringBefore("-")
            return cleaned.split(".")
                .mapNotNull { it.filter(Char::isDigit).toIntOrNull() }
        }
    }
}

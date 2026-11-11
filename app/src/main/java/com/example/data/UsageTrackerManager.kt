package com.example.data

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.util.Log
import com.example.model.HabitPulseEngine
import com.example.model.InstalledAppInfo
import java.util.Calendar

data class AppUsageData(
    val packageName: String,
    val launches: Int,
    val foregroundTimeMillis: Long
) {
    val foregroundMinutes: Int
        get() = (foregroundTimeMillis / (1000 * 60)).toInt()
}

/**
 * Robust, production-grade UsageStats manager:
 * - Comprehensive permission checking via AppOpsManager with zero crashes
 * - Validated time intervals for queryUsageStats & queryEvents
 * - Robust try-catch shielding around SecurityException and OEM system bugs
 * - Safe per-package iteration with null-safety
 */
class UsageTrackerManager(private val context: Context) {

    companion object {
        private const val TAG = "UsageTrackerManager"
    }

    private val usageStatsManager: UsageStatsManager? by lazy {
        try {
            context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve USAGE_STATS_SERVICE", e)
            null
        }
    }

    /**
     * Checks if PACKAGE_USAGE_STATS is explicitly granted using AppOpsManager.
     * Guaranteed never to throw, even on heavily modified OEM ROMs.
     */
    fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
                ?: return false

            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException checking usage stats permission: ${e.message}")
            false
        } catch (e: Exception) {
            Log.w(TAG, "Unexpected error checking usage stats permission: ${e.message}")
            false
        }
    }

    /**
     * Creates an intent to navigate the user directly to the Usage Access Settings page.
     */
    fun createUsageAccessIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    /**
     * Safe Query: Queries UsageStats for today (from midnight to now).
     * 1. Checks permissions first.
     * 2. Validates start and end time boundaries.
     * 3. Wraps entire query in try-catch for SecurityException / RemoteException / IllegalStateException.
     * 4. Safe per-item parsing with null checks.
     */
    fun queryTodayUsageStats(): Map<String, AppUsageData> {
        val mgr = usageStatsManager ?: return emptyMap()
        if (!hasUsageStatsPermission()) {
            return emptyMap()
        }

        val endTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = calendar.timeInMillis

        // Strictly validate time ranges to prevent IllegalArgumentException
        if (startTime >= endTime || startTime <= 0) {
            Log.w(TAG, "Invalid time range for usage stats: startTime=$startTime, endTime=$endTime")
            return emptyMap()
        }

        val resultMap = mutableMapOf<String, AppUsageData>()

        // 1. Primary aggregation via queryUsageStats (safest across all Android versions)
        try {
            val statsList: List<UsageStats>? = mgr.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            if (!statsList.isNullOrEmpty()) {
                for (stat in statsList) {
                    try {
                        val pkg = stat.packageName ?: continue
                        if (pkg.isBlank() || pkg == context.packageName) continue

                        val totalTime = stat.totalTimeInForeground
                        if (totalTime > 0) {
                            val existing = resultMap[pkg]
                            val combinedTime = (existing?.foregroundTimeMillis ?: 0L) + totalTime
                            resultMap[pkg] = AppUsageData(
                                packageName = pkg,
                                launches = (existing?.launches ?: 0).coerceAtLeast(1),
                                foregroundTimeMillis = combinedTime
                            )
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error parsing single UsageStats item", e)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException during queryUsageStats", e)
            return emptyMap()
        } catch (e: Exception) {
            Log.w(TAG, "Unexpected error in queryUsageStats", e)
        }

        // 2. Event-level refinement for exact foreground sessions and anti-cheat rule
        try {
            val events = mgr.queryEvents(startTime, endTime)
            if (events != null) {
                val event = UsageEvents.Event()
                val minSessionMillis = HabitPulseEngine.ANTI_CHEAT_MIN_SESSION_MILLIS
                val lastResumeMap = mutableMapOf<String, Long>()
                val refinedTimeMap = mutableMapOf<String, Long>()
                val refinedLaunchesMap = mutableMapOf<String, Int>()

                while (events.hasNextEvent()) {
                    try {
                        events.getNextEvent(event)
                        val pkg = event.packageName ?: continue
                        if (pkg.isBlank() || pkg == context.packageName) continue

                        when (event.eventType) {
                            UsageEvents.Event.ACTIVITY_RESUMED -> {
                                lastResumeMap[pkg] = event.timeStamp
                            }
                            UsageEvents.Event.ACTIVITY_PAUSED,
                            UsageEvents.Event.ACTIVITY_STOPPED -> {
                                val resumedTime = lastResumeMap[pkg]
                                if (resumedTime != null && event.timeStamp > resumedTime) {
                                    val sessionDuration = event.timeStamp - resumedTime
                                    if (sessionDuration >= minSessionMillis) {
                                        refinedTimeMap[pkg] = (refinedTimeMap[pkg] ?: 0L) + sessionDuration
                                        refinedLaunchesMap[pkg] = (refinedLaunchesMap[pkg] ?: 0) + 1
                                    }
                                    lastResumeMap.remove(pkg)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Error processing individual usage event", e)
                    }
                }

                // Check ongoing foreground session
                for ((pkg, resumedTime) in lastResumeMap) {
                    try {
                        if (pkg == context.packageName) continue
                        val ongoingDuration = endTime - resumedTime
                        if (ongoingDuration >= minSessionMillis) {
                            refinedTimeMap[pkg] = (refinedTimeMap[pkg] ?: 0L) + ongoingDuration
                            refinedLaunchesMap[pkg] = (refinedLaunchesMap[pkg] ?: 0) + 1
                        }
                    } catch (_: Exception) {}
                }

                if (refinedTimeMap.isNotEmpty()) {
                    for (pkg in refinedTimeMap.keys + refinedLaunchesMap.keys) {
                        resultMap[pkg] = AppUsageData(
                            packageName = pkg,
                            launches = refinedLaunchesMap[pkg] ?: (resultMap[pkg]?.launches ?: 1),
                            foregroundTimeMillis = refinedTimeMap[pkg] ?: (resultMap[pkg]?.foregroundTimeMillis ?: 0L)
                        )
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException during queryEvents", e)
        } catch (e: Exception) {
            Log.w(TAG, "Unexpected error in queryEvents", e)
        }

        return resultMap
    }

    /**
     * Lists user-launchable installed apps safely without crashing on missing permissions or PackageManager glitches.
     */
    fun getInstalledLaunchableApps(): List<InstalledAppInfo> {
        return try {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfoList = pm.queryIntentActivities(intent, 0)
            val list = mutableListOf<InstalledAppInfo>()

            for (info in resolveInfoList) {
                try {
                    val pkg = info.activityInfo?.packageName ?: continue
                    if (pkg.isBlank() || pkg == context.packageName) continue

                    val label = try {
                        info.loadLabel(pm).toString()
                    } catch (_: Exception) {
                        pkg.substringAfterLast('.').replaceFirstChar { it.uppercase() }
                    }

                    list.add(InstalledAppInfo(packageName = pkg, appName = label))
                } catch (e: Exception) {
                    Log.w(TAG, "Error resolving single app info", e)
                }
            }

            list.distinctBy { it.packageName }.sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to getInstalledLaunchableApps", e)
            emptyList()
        }
    }

    /**
     * Helper to resolve app label for a given package name safely.
     */
    fun getAppLabel(packageName: String): String {
        return try {
            val pm = context.packageManager
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (_: Exception) {
            packageName.substringAfterLast('.').replaceFirstChar { it.uppercase() }
        }
    }
}

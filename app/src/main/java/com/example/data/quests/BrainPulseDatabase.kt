package com.example.data.quests

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [QuestEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(QuestTypeConverters::class)
abstract class BrainPulseDatabase : RoomDatabase() {

    abstract fun questDao(): QuestDao

    companion object {
        @Volatile
        private var INSTANCE: BrainPulseDatabase? = null

        fun getInstance(context: Context): BrainPulseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BrainPulseDatabase::class.java,
                    "brain_pulse_quests.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

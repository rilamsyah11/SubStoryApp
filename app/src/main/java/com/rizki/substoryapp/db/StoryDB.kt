package com.rizki.substoryapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rizki.substoryapp.response.ListStory

@Database(
    entities = [ListStory::class],
    version = 1,
    exportSchema = false
)

abstract class StoryDB : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: StoryDB? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDB::class.java, "story_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
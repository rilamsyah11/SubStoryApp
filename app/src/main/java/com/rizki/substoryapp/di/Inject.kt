package com.rizki.substoryapp.di

import android.content.Context
import com.rizki.substoryapp.data.StoryRepository
import com.rizki.substoryapp.db.StoryDB
import com.rizki.substoryapp.retrofit.ConfigApi

object Inject {
    fun setRepository(context: Context): StoryRepository {
        val db = StoryDB.getDatabase(context)
        val serviceApi = ConfigApi.getServiceApi()

        return StoryRepository(db, serviceApi)
    }
}
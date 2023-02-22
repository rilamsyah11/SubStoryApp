package com.rizki.substoryapp.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rizki.substoryapp.model.UsersAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UsersPreference private constructor(private val DataStore: DataStore<Preferences>) {

    fun getUsers() : Flow<UsersAuth> {
        return DataStore.data.map {preference ->
            UsersAuth(
                preference[KEY_TOKEN] ?:"",
                preference[KEY_STATE] ?: false
            )
        }
    }

    suspend fun saveUsers(users: UsersAuth) {
        DataStore.edit {preference ->
            preference[KEY_TOKEN] = users.Token
            preference[KEY_STATE] = users.IsLogin
        }
    }

    suspend fun logoutUsers() {
        DataStore.edit {preference ->
            preference[KEY_STATE] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCES: UsersPreference? = null

        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_STATE = booleanPreferencesKey("state")

        fun getInstances(DataStore: DataStore<Preferences>) : UsersPreference {
            return INSTANCES ?: synchronized(this) {
                val instances = UsersPreference(DataStore)
                INSTANCES = instances
                instances
            }
        }
    }
}
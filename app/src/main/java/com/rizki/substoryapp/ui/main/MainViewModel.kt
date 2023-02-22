package com.rizki.substoryapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rizki.substoryapp.model.UsersAuth
import com.rizki.substoryapp.preference.UsersPreference
import kotlinx.coroutines.launch

class MainViewModel(private val preference: UsersPreference) : ViewModel() {
    fun getUsers() : LiveData<UsersAuth> {
        return preference.getUsers().asLiveData()
    }

    fun saveUsers(users: UsersAuth) {
        viewModelScope.launch {
            preference.saveUsers(users)
        }
    }

    fun logoutUsers() {
        viewModelScope.launch {
            preference.logoutUsers()
        }
    }
}
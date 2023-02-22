package com.rizki.substoryapp.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rizki.substoryapp.ui.main.MainViewModel
import com.rizki.substoryapp.preference.UsersPreference

class ViewsModelFactory(private val preference: UsersPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference) as T
            }
            else -> throw IllegalArgumentException("Unknown class ViewModel : " + modelClass.name)
        }
    }
}
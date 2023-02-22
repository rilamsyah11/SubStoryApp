package com.rizki.substoryapp.ui.main.stories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rizki.substoryapp.data.StoryRepository
import com.rizki.substoryapp.di.Inject
import com.rizki.substoryapp.response.ListStory

class StoriesViewModel(private val storiesRepository: StoryRepository) : ViewModel() {

    fun story(header: String): LiveData<PagingData<ListStory>> = storiesRepository.getStoriesForPaging(header).cachedIn(viewModelScope)

    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T: ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(StoriesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StoriesViewModel(Inject.setRepository(context)) as T
            }
            else throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
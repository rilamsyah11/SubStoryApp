package com.rizki.substoryapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.rizki.substoryapp.db.StoryDB
import com.rizki.substoryapp.response.ListStory
import com.rizki.substoryapp.retrofit.ServiceApi

class StoryRepository(private val storyDB: StoryDB, private val serviceApi: ServiceApi) {
    fun getStoriesForPaging(header: String) : LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                StoryPagingSource(serviceApi, header)
            }
        ).liveData
    }
}
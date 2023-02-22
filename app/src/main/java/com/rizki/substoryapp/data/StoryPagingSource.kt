package com.rizki.substoryapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rizki.substoryapp.response.ListStory
import com.rizki.substoryapp.retrofit.ServiceApi

class StoryPagingSource (private val serviceApi: ServiceApi, private val header: String) : PagingSource<Int, ListStory>() {

    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPages = state.closestPageToPosition(anchorPosition)
            anchorPages?.prevKey?.plus(1) ?: anchorPages?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val pages = params.key ?: PAGE_INDEX
            val dataResponse = serviceApi.getStory(header, pages, params.loadSize)

            LoadResult.Page(
                data = dataResponse.ListStory,
                prevKey = if(pages == PAGE_INDEX) null else pages - 1,
                nextKey = if(dataResponse.ListStory.isNullOrEmpty()) null else pages + 1
            )
        } catch(exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val PAGE_INDEX = 1
    }
}
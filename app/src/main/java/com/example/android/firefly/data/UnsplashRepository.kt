package com.example.android.firefly.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.android.firefly.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository
@Inject
constructor(private val unsplashApi: UnsplashApi) {

    fun getSearchResults(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                UnsplashPagingSourceSearch(unsplashApi, query)
            }).liveData  //Pager return a stream of PagingData objects like PagingData<UnsplashPhoto> and allows us to define data stream type,
                         // hence this returns LiveData<PagingData<UnsplashPhoto>>

    fun getRandomResults() =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ), pagingSourceFactory = {
                UnsplashPagingSourceRandom(unsplashApi)
            }).liveData
}
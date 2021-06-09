package com.example.android.firefly.data

import androidx.paging.PagingSource
import com.example.android.firefly.api.UnsplashApi
import com.example.android.firefly.constants.Constants.PAGING_START_INDEX
import retrofit2.HttpException
import java.io.IOException

class UnsplashPagingSourceSearch(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: PAGING_START_INDEX

        return try {
            val response = unsplashApi.searchPhotos(query, position, params.loadSize)
            val photos = response.results

            LoadResult.Page(
                data = photos,
                prevKey = if (position == PAGING_START_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (e: IOException) { //can occur when no Internet connection
            LoadResult.Error(e)
        } catch (e: HttpException) {   //error in request
            LoadResult.Error(e)
        }
    }
}
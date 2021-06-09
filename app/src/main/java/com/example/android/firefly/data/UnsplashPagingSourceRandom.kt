package com.example.android.firefly.data

import androidx.paging.PagingSource
import com.example.android.firefly.api.UnsplashApi
import com.example.android.firefly.constants.Constants
import retrofit2.HttpException
import java.io.IOException

class UnsplashPagingSourceRandom(
    private val unsplashApi: UnsplashApi
) : PagingSource<Int, UnsplashPhoto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: Constants.PAGING_START_INDEX

        return try {
            val photos = unsplashApi.getPhotos(position,params.loadSize)

            LoadResult.Page(
                data = photos,
                prevKey = if (position == Constants.PAGING_START_INDEX) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (e: IOException) { //can occur when no Internet connection
            LoadResult.Error(e)
        } catch (e: HttpException) {   //error in request
            LoadResult.Error(e)
        }
    }
}
package com.example.android.firefly

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.android.firefly.constants.Constants
import com.example.android.firefly.data.UnsplashRepository

class MainViewModel
@ViewModelInject
constructor(private val repository: UnsplashRepository) : ViewModel() {

    //This default is not set through GalleryFragment as every time that fragment is recreated,
    //it sets currentQuery as value of args sent from RandomFragment
    private val currentQuery = MutableLiveData<String>(Constants.DEFAULT)

    fun searchPhotosQuery(query: String) {
        currentQuery.value = query
    }

    val photos =        //It's called whenever the result of currentQuery changes
        currentQuery.switchMap { queryString ->
            repository.getSearchResults(queryString)
                .cachedIn(viewModelScope)
        }
    val randomPhotos = repository.getRandomResults().cachedIn(viewModelScope)
}
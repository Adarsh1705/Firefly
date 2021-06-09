package com.example.android.firefly.ui.gallery

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.firefly.databinding.LoadStateLayoutBinding

class UnsplashLoadStateAdapter:LoadStateAdapter<UnsplashLoadStateAdapter.LoadStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        TODO("Not yet implemented")
    }

    class LoadStateViewHolder(private val binding:LoadStateLayoutBinding):RecyclerView.ViewHolder(binding.root){

    }
}
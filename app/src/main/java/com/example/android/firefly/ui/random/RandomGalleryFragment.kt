package com.example.android.firefly.ui.random

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.firefly.R
import com.example.android.firefly.data.UnsplashPhoto
import com.example.android.firefly.databinding.FragmentRandomGalleryBinding
import com.example.android.firefly.MainViewModel
import com.example.android.firefly.constants.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RandomGalleryFragment : Fragment(R.layout.fragment_random_gallery),
    UnsplashRandomPhotoAdapter.OnItemClickistener {

    //Instead of instantiating view model the old way, we delegate this to viewModels
    private val viewModel by viewModels<MainViewModel>()

    private var _binding: FragmentRandomGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var myRandomAdapter: UnsplashRandomPhotoAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRandomGalleryBinding.bind(view)
        myRandomAdapter = UnsplashRandomPhotoAdapter(this)

        binding.apply {
            recyclerView.adapter = myRandomAdapter
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.hasFixedSize()
        }
        viewModel.randomPhotos.observe(viewLifecycleOwner) {
            myRandomAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
        setHasOptionsMenu(true)
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val action =
            RandomGalleryFragmentDirections.actionRandomGalleryFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_gallery, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.app_bar_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    Constants.DEFAULT=query
                    val action =
                        RandomGalleryFragmentDirections.actionRandomGalleryFragmentToGalleryFragment2(
                            query
                        )
                    findNavController().navigate(action)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.example.android.firefly.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.firefly.MainViewModel
import com.example.android.firefly.R
import com.example.android.firefly.data.UnsplashPhoto
import com.example.android.firefly.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery),
    UnsplashPhotoAdapter.OnItemClickistener {

    //Instead of instantiating view model the old way, we delegate this to viewModels
    private val viewModel by viewModels<MainViewModel>()

    //Same as above comment
    private val args: GalleryFragmentArgs by navArgs()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var myAdapter: UnsplashPhotoAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGalleryBinding.bind(view)
        myAdapter = UnsplashPhotoAdapter(this)

        binding.apply {
            recyclerView.adapter = myAdapter
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.hasFixedSize()
        }

        viewModel.photos.observe(viewLifecycleOwner) {
            myAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        setHasOptionsMenu(true)
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val action = GalleryFragmentDirections.actionGalleryFragment2ToDetailsFragment(photo)
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
                    viewModel.searchPhotosQuery(query)
                    binding.recyclerView.scrollToPosition(0)
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



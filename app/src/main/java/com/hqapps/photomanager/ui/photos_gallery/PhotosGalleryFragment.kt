package com.hqapps.photomanager.ui.photos_gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.hqapps.photomanager.R
import kotlinx.android.synthetic.main.fragment_photos_gallery.*

class PhotosGalleryFragment : Fragment() {

    private lateinit var viewModel: PhotosGalleryViewModel
    private val adapter = PhotosGalleryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PhotosGalleryViewModel::class.java)
        viewModel.encryptedFiles.observe(this, Observer {
            adapter.refreshData(it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photos_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_view.layoutManager = GridLayoutManager(requireContext(), 2)
        recycler_view.adapter = adapter
        viewModel.loadData(requireContext())
    }
}
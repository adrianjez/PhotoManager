package com.hqapps.photomanager.ui.photos_list

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.hqapps.photomanager.R
import com.hqapps.photomanager.utils.FileEncryptManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_photos_list.*
import java.io.File
import java.io.IOException


class PhotosListFragment : Fragment() {

    private var photoPath: String? = null
    private lateinit var viewModel: PhotosListViewModel

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PhotosListViewModel::class.java)
        viewModel.encryptionStateLiveData.observe(this, Observer(::onEncryptionStateChange))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = layoutInflater.inflate(R.layout.fragment_photos_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        take_photo_button.setOnClickListener {
            dispatchTakePictureIntent()
        }
        encrypt_photo.setOnClickListener {
            photoPath?.let {
                viewModel.encryptPhoto(
                    bitmap = (imageView.drawable as BitmapDrawable).bitmap,
                    filePath = it,
                    context = requireContext()
                )
            }

        }
        go_to_gallery.setOnClickListener {
            findNavController().navigate(
                PhotosListFragmentDirections.actionPhotosFragmentToPhotosGallery()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    FileEncryptManager.getTempFile(requireContext())
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    photoPath = it.absolutePath
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(), "com.example.android.fileprovider", it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun setPic() {
        photoPath?.let {
            Picasso.get().load(Uri.fromFile(File(photoPath))).into(imageView)
        }
    }

    private fun onEncryptionStateChange(state: PhotosListViewModel.EncryptionState){
        when(state){
            PhotosListViewModel.EncryptionState.IN_PROGRESS -> {
                progress_view.visibility = View.VISIBLE
            }
            PhotosListViewModel.EncryptionState.SUCCESS -> {
                progress_view.visibility = View.GONE
                displayMessage(getString(R.string.success_message))
            }
            PhotosListViewModel.EncryptionState.ERROR -> {
                progress_view.visibility = View.GONE
                displayMessage(getString(R.string.error_message))
            }
        }
    }

    private fun displayMessage(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
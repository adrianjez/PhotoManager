package com.hqapps.photomanager.ui.photos_gallery

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hqapps.photomanager.R
import com.hqapps.photomanager.utils.FileEncryptManager
import kotlinx.android.synthetic.main.fragment_photos_gallery_view_holder.view.*
import kotlinx.coroutines.*
import java.io.File

class PhotosGalleryAdapter :
    RecyclerView.Adapter<PhotosGalleryAdapter.ViewHolder>() {

    private var data: ArrayList<File> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_photos_gallery_view_holder, parent, false
            )
        )

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(data[position])

    fun refreshData(withNewData: Array<File>){
        data.clear()
        data.addAll(withNewData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        itemView: View,
        val image: ImageView = itemView.image_view
    ) : RecyclerView.ViewHolder(itemView) {

        private var decryptJob: Job? = null

        fun bind(file: File) {
            decryptJob?.cancel()
            decryptJob = GlobalScope.launch {
                val photoData = FileEncryptManager.decrypt(file.absolutePath, itemView.context)
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.size)
                withContext(Dispatchers.Main) {
                    image.setImageBitmap(bitmap)
                }
            }
        }
    }

}
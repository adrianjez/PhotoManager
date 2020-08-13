package com.hqapps.photomanager.ui.photos_gallery

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hqapps.photomanager.base.BaseViewModel
import com.hqapps.photomanager.utils.FileEncryptManager
import kotlinx.coroutines.launch
import java.io.File

class PhotosGalleryViewModel : BaseViewModel(){

    val encryptedFiles = MutableLiveData<Array<File>>()

    fun loadData(context: Context){
        viewModelScope.launch {
            encryptedFiles.postValue(
                FileEncryptManager.getEncryptedFiles(context)
            )
        }
    }
}
package com.hqapps.photomanager.ui.photos_list

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hqapps.photomanager.base.BaseViewModel
import com.hqapps.photomanager.utils.FileEncryptManager
import kotlinx.coroutines.launch


class PhotosListViewModel : BaseViewModel() {

    enum class EncryptionState {
        IN_PROGRESS, SUCCESS, ERROR
    }
    val encryptionStateLiveData = MutableLiveData<EncryptionState>()

    fun encryptPhoto(bitmap: Bitmap, filePath: String, context: Context) {
        encryptionStateLiveData.postValue(EncryptionState.IN_PROGRESS)
        viewModelScope.launch {
            try {
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.let { pathToFiles ->
                    FileEncryptManager.encryptBitmap(
                        bitmap = bitmap,
                        filePath = filePath,
                        pathToFiles = pathToFiles.absolutePath,
                        context = context
                    )
                }
                encryptionStateLiveData.postValue(EncryptionState.SUCCESS)
            } catch (e: Exception){
                e.printStackTrace()
                encryptionStateLiveData.postValue(EncryptionState.ERROR)
            }
        }
    }

}
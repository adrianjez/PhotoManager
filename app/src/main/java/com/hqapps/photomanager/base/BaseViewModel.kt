package com.hqapps.photomanager.base

import androidx.lifecycle.ViewModel
import com.hqapps.photomanager.di.component.DaggerViewModelInjector
import com.hqapps.photomanager.di.component.ViewModelInjector
import com.hqapps.photomanager.di.module.PhotosModule
import com.hqapps.photomanager.ui.login.LoginViewModel
import com.hqapps.photomanager.ui.photos_gallery.PhotosGalleryViewModel
import com.hqapps.photomanager.ui.photos_list.PhotosListViewModel

abstract class BaseViewModel: ViewModel(){

    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .photosModule(PhotosModule)
        .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is PhotosListViewModel -> injector.inject(this)
            is LoginViewModel -> injector.inject(this)
            is PhotosGalleryViewModel -> injector.inject(this)
        }
    }
}
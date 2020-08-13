package com.hqapps.photomanager.di.component

import com.hqapps.photomanager.di.module.PhotosModule
import com.hqapps.photomanager.ui.login.LoginViewModel
import com.hqapps.photomanager.ui.photos_gallery.PhotosGalleryViewModel
import com.hqapps.photomanager.ui.photos_list.PhotosListViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(PhotosModule::class)])
interface ViewModelInjector {

    fun inject(postListViewModel: PhotosListViewModel)
    fun inject(photosGallery: PhotosGalleryViewModel)
    fun inject(loginViewModel: LoginViewModel)

    @Component.Builder
    interface Builder {
        fun build(): ViewModelInjector

        fun photosModule(networkModule: PhotosModule): Builder
    }
}
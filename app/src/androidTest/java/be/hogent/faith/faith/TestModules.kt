package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.takePhoto.TakePhotoViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val fragmentTestModule = module(override = true) {
    viewModel { TakePhotoViewModel(get(), Event()) }
}
package be.hogent.faith.faith.di

import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.createUser.CreateEventViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.mainScreen.MainScreenViewModel
import be.hogent.faith.faith.takePhoto.FotoApparatFacade
import be.hogent.faith.faith.takePhoto.TakePhotoViewModel
import be.hogent.faith.service.usecases.interfaces.PhotoTaker
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.io.File

val appModule = module {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    single { FotoApparatFacade(get()) as PhotoTaker }

    // ViewModels
    viewModel { CreateEventViewModel(get(), get()) }
    viewModel { MainScreenViewModel() }
    viewModel { EventDetailsViewModel() }
    viewModel { DrawEmotionViewModel() }
    viewModel { (tempPhotoFile: File, event: Event) -> TakePhotoViewModel(get(), tempPhotoFile, event) }
}
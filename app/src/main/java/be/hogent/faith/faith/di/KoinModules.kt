package be.hogent.faith.faith.di

import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.createUser.CreateEventViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.mainScreen.MainScreenViewModel
import be.hogent.faith.faith.recordAudio.RecordAudioViewModel
import be.hogent.faith.service.usecases.SaveAudioRecordingUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.io.File

val appModule = module {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    // ViewModels
    viewModel { CreateEventViewModel(get(), get()) }
    viewModel { MainScreenViewModel() }
    viewModel { EventDetailsViewModel() }
    viewModel { DrawEmotionViewModel() }
    viewModel { (tempRecordingFile: File, event: Event) ->
        RecordAudioViewModel(get(), tempRecordingFile, event)
    }
}
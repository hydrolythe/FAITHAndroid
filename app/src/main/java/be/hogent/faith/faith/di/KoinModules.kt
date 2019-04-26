package be.hogent.faith.faith.di

import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.util.AvatarProvider
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.editDetail.EditDetailViewModel
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.mainScreen.MainScreenViewModel
import be.hogent.faith.faith.overviewEvents.OverviewEventsViewModel
import be.hogent.faith.faith.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.util.TempFileProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.util.UUID

val appModule = module(override = true) {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    // ViewModels
    viewModel { MainScreenViewModel() }
    viewModel { (user: User, eventUuid: UUID?) -> EventDetailsViewModel(get(), user, eventUuid) }
    viewModel { (user: User) -> EventDetailsViewModel(get(), user) }
    viewModel { DrawEmotionViewModel() }
    viewModel { EditDetailViewModel() }
    viewModel { UserViewModel(get()) }
    viewModel { (user: LiveData<User>) -> OverviewEventsViewModel(user) }
    viewModel { AvatarViewModel(get(), get()) }
    viewModel { (event: Event) -> RecordAudioViewModel(get(), event) }
    viewModel { (event: Event) -> TakePhotoViewModel(get(), event) }

    single { TempFileProvider(androidContext()) }
    single { AvatarProvider(androidContext()) }
}
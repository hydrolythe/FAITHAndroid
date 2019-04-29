package be.hogent.faith.faith.di

import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.cityScreen.CityScreenViewModel
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_ID
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.overviewEvents.OverviewEventsViewModel
import be.hogent.faith.faith.registerAvatar.AvatarViewModel
import be.hogent.faith.faith.registerAvatar.UserViewModel
import be.hogent.faith.faith.util.AvatarProvider
import be.hogent.faith.faith.util.TempFileProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.util.UUID

object KoinModules {
    const val USER_SCOPE_ID = "USER_SCOPE"
}

val appModule = module(override = true) {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    // ViewModels
    viewModel { CityScreenViewModel() }
    viewModel { (user: LiveData<User>, eventUuid: UUID?) -> EventViewModel(get(), get(), user, eventUuid) }
    viewModel { (user: LiveData<User>) -> EventViewModel(get(), get(), user) }
    viewModel { DrawEmotionViewModel() }
    viewModel { EditDetailViewModel() }
    viewModel { (user: LiveData<User>) -> OverviewEventsViewModel(user) }
    viewModel { AvatarViewModel(get(), get()) }
    viewModel { (event: Event) -> RecordAudioViewModel(get(), event) }
    viewModel { (event: Event) -> TakePhotoViewModel(get(), event) }

    // UserViewModel is scoped and not just shared because it is used over multiple activities.
    // Scope is opened when logging in a new user and closed when logging out.
    scope(USER_SCOPE_ID) {
        UserViewModel()
    }

    single { TempFileProvider(androidContext()) }
    single { AvatarProvider(androidContext()) }
}
package be.hogent.faith.faith.di

import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.cityScreen.CityScreenViewModel
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_ID
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel
import be.hogent.faith.faith.emotionCapture.drawing.makeDrawing.PremadeImagesProvider
import be.hogent.faith.faith.emotionCapture.drawing.makeDrawing.PremadeImagesProviderFromResources
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.enterText.EnterTextViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.loginOrRegister.AvatarProvider
import be.hogent.faith.faith.loginOrRegister.AvatarViewModel
import be.hogent.faith.faith.loginOrRegister.ResourceAvatarProvider
import be.hogent.faith.faith.loginOrRegister.WelcomeViewModel
import be.hogent.faith.faith.overviewEvents.OverviewEventsViewModel
import be.hogent.faith.faith.util.TempFileProvider
import com.auth0.android.Auth0
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
    viewModel { (eventUuid: UUID?) -> EventViewModel(get(), get(), get(), get(), get(), eventUuid) }
    viewModel { EventViewModel(get(), get(), get(), get(), get()) }
    viewModel { DrawViewModel() }
    viewModel { EditDetailViewModel() }
    viewModel { EnterTextViewModel() }
    viewModel { OverviewEventsViewModel() }
    viewModel { AvatarViewModel(get(), get()) }
    viewModel { WelcomeViewModel() }
    viewModel { RecordAudioViewModel() }
    viewModel { TakePhotoViewModel() }

    // UserViewModel is scoped and not just shared because it is used over multiple activities.
    // Scope is opened when logging in a new user and closed when logging out.
    scope(USER_SCOPE_ID) {
        UserViewModel(get())
    }

    single { TempFileProvider(androidContext()) }
    single { ResourceAvatarProvider(androidContext()) as AvatarProvider }

    single { PremadeImagesProviderFromResources() as PremadeImagesProvider }

    single{ Auth0(androidContext()) }
}
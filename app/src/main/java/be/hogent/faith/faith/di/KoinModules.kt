package be.hogent.faith.faith.di

import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.cityScreen.CityScreenViewModel
import be.hogent.faith.faith.di.KoinModules.DRAWING_SCOPE_NAME
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import be.hogent.faith.faith.details.drawing.create.DrawViewModel
import be.hogent.faith.faith.details.drawing.create.DrawingDetailViewModel
import be.hogent.faith.faith.details.drawing.create.draggableImages.PremadeImagesProvider
import be.hogent.faith.faith.details.drawing.create.draggableImages.PremadeImagesProviderFromResources
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.details.text.create.TextDetailViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.AudioViewModel
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.library.eventDetailsList.SelectedItemViewModel
import be.hogent.faith.faith.loginOrRegister.LoginManager
import be.hogent.faith.faith.loginOrRegister.RegisterUserViewModel
import be.hogent.faith.faith.loginOrRegister.WelcomeViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.ResourceAvatarProvider
import be.hogent.faith.faith.loginOrRegister.registerUserInfo.RegisterUserInfoViewModel
import be.hogent.faith.faith.overviewEvents.OverviewEventsViewModel
import be.hogent.faith.faith.util.AndroidTempFileProvider
import be.hogent.faith.faith.util.TempFileProvider
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object KoinModules {
    const val USER_SCOPE_NAME = "USER_SCOPE_NAME"
    const val USER_SCOPE_ID = "USER_SCOPE_ID"

    const val DRAWING_SCOPE_NAME = "DRAWING_SCOPE_NAME"
    const val DRAWING_SCOPE_ID = "DRAWING_SCOPE_ID"
}

val appModule = module(override = true) {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    // ViewModels
    viewModel { CityScreenViewModel() }
    viewModel { (event: Event) -> EventViewModel(get(), get(), get(), get(), get(), event) }
    viewModel { EventViewModel(get(), get(), get(), get(), get()) }
    viewModel { DrawingDetailViewModel(null, get(), get()) }
    viewModel { EditDetailViewModel() }
    viewModel { TextDetailViewModel(get(), get(), get()) }
    viewModel { OverviewEventsViewModel() }
    viewModel { RegisterAvatarViewModel(get()) }
    viewModel { WelcomeViewModel() }
    viewModel { AudioViewModel() }
    viewModel { RegisterUserViewModel(get()) }
    viewModel { RegisterUserInfoViewModel() }
    viewModel { TakePhotoViewModel() }
    viewModel { SelectedItemViewModel() }

    // UserViewModel is scoped and not just shared because it is used over multiple activities.
    // Scope is opened when logging in a new user and closed when logging out.
    scope(named(USER_SCOPE_NAME)) {
        scoped { UserViewModel(get()) }
    }

    // Both the normal DrawingFragment and the DrawAvatarFragment need a DrawViewModel.
    // The DrawAvatarFragment uses a shared VM from the EmotionCapturyActivity because
    // there's only one EmotionAvatar per EmotionCapture.
    // The DrawingFragment should retain its VM until the drawing is saved
    scope(named(DRAWING_SCOPE_NAME)) {
        scoped { DrawViewModel() }
    }

    single { AndroidTempFileProvider(androidContext()) as TempFileProvider }

    single { LoginManager() }

    single { ResourceAvatarProvider(androidContext()) as AvatarProvider }

    single { PremadeImagesProviderFromResources() as PremadeImagesProvider }

    // Dependency injection for the login, authentication
    single { Auth0(androidContext()) }
    single { AuthenticationAPIClient(get() as Auth0) }
    // We are using SharedPrefs to store tokens, in PRIVATE mode
    single {
        SecureCredentialsManager(
            get(),
            get() as AuthenticationAPIClient,
            get() as SharedPreferencesStorage
        )
    }
    single { SharedPreferencesStorage(androidContext()) }
}
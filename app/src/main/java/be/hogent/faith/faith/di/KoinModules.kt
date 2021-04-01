package be.hogent.faith.faith.di

import android.app.Application
import android.content.Context
import android.os.Build
import be.hogent.faith.faith.models.TreasureChest
import be.hogent.faith.faith.IUserRepository
import be.hogent.faith.faith.UserRepository
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.backpack.BackpackRepository
import be.hogent.faith.faith.backpack.BackpackViewModel
import be.hogent.faith.faith.cinema.CinemaCreateVideoViewModel
import be.hogent.faith.faith.cinema.CinemaOverviewViewModel
import be.hogent.faith.faith.cinema.CinemaRepository
import be.hogent.faith.faith.cityScreen.CityScreenRepository
import be.hogent.faith.faith.cityScreen.CityScreenViewModel
import be.hogent.faith.faith.cityScreen.ICityScreenRepository
import be.hogent.faith.faith.details.BackpackDetailsMetaDataViewModel
import be.hogent.faith.faith.details.CinemaDetailsMetaDataViewModel
import be.hogent.faith.faith.details.TreasureChestDetailsMetaDataViewModel
import be.hogent.faith.faith.details.audio.AudioDetailRepository
import be.hogent.faith.faith.details.audio.AudioDetailViewModel
import be.hogent.faith.faith.details.audio.IAudioDetailRepository
import be.hogent.faith.faith.details.drawing.create.DrawViewModel
import be.hogent.faith.faith.details.drawing.create.DrawingDetailRepository
import be.hogent.faith.faith.details.drawing.create.DrawingDetailViewModel
import be.hogent.faith.faith.details.drawing.create.IDrawingDetailRepository
import be.hogent.faith.faith.details.drawing.create.draggableImages.PremadeImagesProvider
import be.hogent.faith.faith.details.drawing.create.draggableImages.PremadeImagesProviderFromResources
import be.hogent.faith.faith.details.drawing.view.ViewDrawingDetailViewModel
import be.hogent.faith.faith.details.externalFile.ExternalFileViewModel
import be.hogent.faith.faith.details.photo.create.CreatePhotoRepository
import be.hogent.faith.faith.details.photo.create.ICreatePhotoRepository
import be.hogent.faith.faith.details.photo.create.TakePhotoViewModel
import be.hogent.faith.faith.details.photo.view.ViewPhotoDetailViewModel
import be.hogent.faith.faith.details.text.create.ITextDetailRepository
import be.hogent.faith.faith.details.text.create.TextDetailRepository
import be.hogent.faith.faith.details.text.create.TextDetailViewModel
import be.hogent.faith.faith.details.text.view.ViewTextDetailViewModel
import be.hogent.faith.faith.details.video.view.ViewVideoViewModel
import be.hogent.faith.faith.details.youtubeVideo.create.YoutubeVideoDetailViewModel
import be.hogent.faith.faith.detailscontainer.IDetailsContainerRepository
import be.hogent.faith.faith.di.KoinModules.DRAWING_SCOPE_NAME
import be.hogent.faith.faith.di.KoinModules.USER_SCOPE_NAME
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.library.eventDetails.EventDetailsViewModel
import be.hogent.faith.faith.library.eventList.EventListViewModel
import be.hogent.faith.faith.loginOrRegister.AuthManager
import be.hogent.faith.faith.loginOrRegister.FirebaseAuthManager
import be.hogent.faith.faith.loginOrRegister.IAuthManager
import be.hogent.faith.faith.loginOrRegister.LoginUserUseCase
import be.hogent.faith.faith.loginOrRegister.WelcomeViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.AvatarProvider
import be.hogent.faith.faith.loginOrRegister.registerAvatar.IRegisterAvatarRepository
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.ResourceAvatarProvider
import be.hogent.faith.faith.models.Backpack
import be.hogent.faith.faith.models.Cinema
import be.hogent.faith.faith.models.Event
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.goals.Goal
import be.hogent.faith.faith.skyscraper.goal.GoalViewModel
import be.hogent.faith.faith.skyscraper.startscreen.SkyscraperOverviewViewModel
import be.hogent.faith.faith.treasureChest.TreasureChestViewModel
import be.hogent.faith.faith.util.AndroidTempFileProvider
import be.hogent.faith.faith.util.TempFileProvider
import be.hogent.faith.faith.videoplayer.CurrentVideoViewModel
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarRepository
import be.hogent.faith.faith.treasureChest.TreasureChestRepository
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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

    // Observing scheduler for use cases
    single { AndroidSchedulers.mainThread() }
    single { AuthManager(get()) as IAuthManager }
    single { FirebaseAuthManager(constructFirebaseAuthInstance()) }
    single<IUserRepository> { UserRepository(constructFirebaseAuthInstance()) }
    single<IRegisterAvatarRepository> { RegisterAvatarRepository() }
    single { BackpackRepository(constructFirebaseAuthInstance()) }
    single { CinemaRepository() }
    single { TreasureChestRepository() }
    single<IAudioDetailRepository>{ AudioDetailRepository() }
    single<IDrawingDetailRepository>{ DrawingDetailRepository() }
    single<ICreatePhotoRepository>{ CreatePhotoRepository() }
    single<ITextDetailRepository>{ TextDetailRepository() }
    single{ CityScreenRepository(constructFirebaseAuthInstance()) }
    factory { LoginUserUseCase(get(), get()) }


    // ViewModels
    viewModel { CityScreenViewModel(get()) }
    viewModel { (event: Event) -> EventViewModel(event) }
    viewModel { (user: User) -> SkyscraperOverviewViewModel(user) }
    viewModel { EventViewModel(get()) }
    viewModel { (backpack: Backpack, context: Context) ->
        BackpackViewModel(
            backpack = backpack,
            backpackRepository = get(),
            context = context
        )
    }
    viewModel { (treasurechest: TreasureChest, context: Context) ->
        TreasureChestViewModel(
            treasureChest = treasurechest,
            treasureChestRepository = get(),
            context = context
        )
    }
    viewModel { (cinema: Cinema, context: Context) ->
        CinemaOverviewViewModel(
            cinema = cinema,
            cinemaRepository = get(),
            context = context
        )
    }
    viewModel { DrawViewModel() }
    viewModel { DrawingDetailViewModel(get()) }
    viewModel { EditDetailViewModel() }
    viewModel { TextDetailViewModel(get()) }
    viewModel { RegisterAvatarViewModel(get(),get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { AudioDetailViewModel(get(),get()) }
    viewModel { WelcomeViewModel(get()) }
    viewModel { TakePhotoViewModel(get()) }
    viewModel { YoutubeVideoDetailViewModel() }
    viewModel { CurrentVideoViewModel() }
    viewModel { ExternalFileViewModel() }
    viewModel { (user: User) -> EventListViewModel(user) }
    viewModel { EventDetailsViewModel() }
    viewModel { ViewPhotoDetailViewModel() }
    viewModel { ViewDrawingDetailViewModel() }
    viewModel { CinemaCreateVideoViewModel() }
    viewModel { CinemaDetailsMetaDataViewModel() }
    viewModel { BackpackDetailsMetaDataViewModel() }
    viewModel { TreasureChestDetailsMetaDataViewModel() }
    viewModel { (goal: Goal, user: User) -> GoalViewModel(goal, user) }
    viewModel { ViewTextDetailViewModel(get()) }

    viewModel { ViewVideoViewModel() }

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
fun constructFirebaseAuthInstance(): FirebaseAuth {
    return FirebaseAuth.getInstance()
}
package be.hogent.faith.faith.di

import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarViewModel
import androidx.lifecycle.LiveData
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.createUser.CreateEventViewModel
import be.hogent.faith.faith.mainScreen.MainScreenViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.overviewEvents.OverviewEventsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.util.UUID

val appModule = module {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    // ViewModels
    viewModel { CreateEventViewModel(get(), get()) }
    viewModel { MainScreenViewModel() }
    viewModel { CreateEventViewModel(get(), get()) }
    viewModel { (user: LiveData<User>, eventUuid: UUID?) -> EventDetailsViewModel(user, eventUuid) }
    viewModel { DrawEmotionViewModel() }
    viewModel { UserViewModel(get()) }
    viewModel { (user: LiveData<User>) -> OverviewEventsViewModel(user) }
    viewModel { AvatarViewModel()}
}
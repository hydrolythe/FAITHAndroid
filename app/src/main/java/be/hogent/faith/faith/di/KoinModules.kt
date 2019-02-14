package be.hogent.faith.faith.di

import be.hogent.faith.faith.createUser.CreateEventViewModel
import be.hogent.faith.faith.mainScreen.MainScreenViewModel
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    // Scheduler for use cases
    single { AndroidSchedulers.mainThread() }

    // ViewModels
    viewModel { CreateEventViewModel(get(), get()) }
    viewModel { MainScreenViewModel() }
    viewModel { DrawEmotionViewModel() }

}
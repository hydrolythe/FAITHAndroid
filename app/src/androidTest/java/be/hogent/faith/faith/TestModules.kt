package be.hogent.faith.faith

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.chooseAvatar.fragments.AvatarViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import be.hogent.faith.faith.chooseAvatar.fragments.UserViewModel
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.mainScreen.MainScreenViewModel

val fragmentTestModule = module(override = true) {
    viewModel { TakePhotoViewModel(get(), Event()) }
    viewModel { RecordAudioViewModel(get(), Event()) }

    viewModel { EventDetailsViewModel(get(), User(), null) }
    viewModel { EventDetailsViewModel(get(), User()) }

        viewModel { UserViewModel() }
    viewModel { DrawEmotionViewModel() }
    viewModel { EditDetailViewModel() }
    viewModel { AvatarViewModel() }
    viewModel { MainScreenViewModel() }
}

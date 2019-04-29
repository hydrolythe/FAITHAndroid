package be.hogent.faith.faith

import androidx.lifecycle.MutableLiveData
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.registerAvatar.AvatarViewModel
import be.hogent.faith.faith.registerAvatar.UserViewModel
import be.hogent.faith.faith.emotionCapture.drawEmotionAvatar.DrawEmotionViewModel
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioViewModel
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoViewModel
import be.hogent.faith.faith.cityScreen.CityScreenViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val fragmentTestModule = module(override = true) {
    viewModel { TakePhotoViewModel(get(), Event()) }
    viewModel { RecordAudioViewModel(get(), Event()) }

    viewModel { UserViewModel() }
    viewModel { DrawEmotionViewModel() }
    viewModel { EditDetailViewModel() }
    viewModel { AvatarViewModel(get(), get()) }
    viewModel { CityScreenViewModel() }
    viewModel { EventViewModel(get(), get(), MutableLiveData<User>()) }
}
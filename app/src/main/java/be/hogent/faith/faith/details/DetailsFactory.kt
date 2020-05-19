package be.hogent.faith.faith.details

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.backpackScreen.SaveBackpackDetailDialog
import be.hogent.faith.faith.backpackScreen.youtubeVideo.create.YoutubeVideoDetailFragment
import be.hogent.faith.faith.backpackScreen.youtubeVideo.view.ViewYoutubeVideoFragment
import be.hogent.faith.faith.cinema.SaveCinemaDetailDialog
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.drawing.view.ViewDrawingFragment
import be.hogent.faith.faith.details.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ViewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.details.text.view.ViewTextDetailFragment
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass

enum class DetailType {
    PHOTO, DRAWING, AUDIO, TEXT, YOUTUBE, EXTERNALFILE
}

enum class DetailMetaDataType {
    EVENT, BACKPACK, CINEMA
}

object DetailsFactory {
    fun createDetail(
        type: DetailType
    ): Fragment {
        return when (type) {
            DetailType.AUDIO -> RecordAudioFragment.newInstance()
            DetailType.TEXT -> TextDetailFragment.newInstance()
            DetailType.DRAWING -> DrawingDetailFragment.newInstance()
            DetailType.PHOTO -> TakePhotoFragment.newInstance()
            DetailType.YOUTUBE -> YoutubeVideoDetailFragment.newInstance()
            DetailType.EXTERNALFILE -> AddExternalFileFragment.newInstance()
        }
    }

    fun editDetail(
        detail: Detail
    ): Fragment {
        return when (detail) {
            is AudioDetail -> RecordAudioFragment.newInstance(detail)
            is TextDetail -> TextDetailFragment.newInstance(detail)
            is DrawingDetail -> DrawingDetailFragment.newInstance(detail)
            is PhotoDetail -> ViewPhotoFragment.newInstance(detail)
            is YoutubeVideoDetail -> ViewYoutubeVideoFragment.newInstance(detail)
            else -> throw UnsupportedOperationException()
        }
    }

    fun viewDetail(detail: Detail): Fragment {
        return when (detail) {
            is AudioDetail -> RecordAudioFragment.newInstance(detail)
            is TextDetail -> ViewTextDetailFragment.newInstance(detail)
            is DrawingDetail -> ViewDrawingFragment.newInstance(detail)
            is PhotoDetail -> ViewPhotoFragment.newInstance(detail)
            is YoutubeVideoDetail -> ViewYoutubeVideoFragment.newInstance(detail)
            else -> ViewPhotoFragment.newInstance(detail as PhotoDetail)
        }
    }

    fun createMetaDataDialog(
        containerType: KClass<FragmentActivity>,
        detailType: KClass<Detail>
    ): DialogFragment? {
        return when (containerType.qualifiedName) {
            "be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity" -> null
            "be.hogent.faith.faith.backpackScreen.BackpackScreenActivity" -> SaveBackpackDetailDialog(detailType)
            else -> SaveCinemaDetailDialog.newInstance(detailType)
        }
    }
}
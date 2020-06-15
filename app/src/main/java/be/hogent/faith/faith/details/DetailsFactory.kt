package be.hogent.faith.faith.details

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.backpack.BackpackScreenActivity
import be.hogent.faith.faith.backpack.SaveBackpackDetailDialog
import be.hogent.faith.faith.cinema.CinemaActivity
import be.hogent.faith.faith.cinema.SaveCinemaDetailDialog
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.drawing.view.ViewDrawingFragment
import be.hogent.faith.faith.details.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ViewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.details.text.view.ViewTextDetailFragment
import be.hogent.faith.faith.details.video.view.ViewVideoFragment
import be.hogent.faith.faith.details.youtubeVideo.create.YoutubeVideoDetailFragment
import be.hogent.faith.faith.details.youtubeVideo.view.ViewYoutubeVideoFragment
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.treasureChest.SaveTreasureChestDetailDialog
import be.hogent.faith.faith.treasureChest.TreasureChestActivity
import kotlin.reflect.KClass

enum class DetailType {
    PHOTO, DRAWING, AUDIO, TEXT, YOUTUBE, EXTERNALFILE
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
            is FilmDetail -> ViewVideoFragment.newInstance(detail)
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
        containerType: FragmentActivity,
        detailType: KClass<Detail>
    ): DialogFragment? {
        return when (containerType) {
            is EmotionCaptureMainActivity -> null
            is BackpackScreenActivity -> SaveBackpackDetailDialog(detailType)
            is CinemaActivity -> SaveCinemaDetailDialog.newInstance(detailType)
            is TreasureChestActivity -> SaveTreasureChestDetailDialog.newInstance(detailType)
            else -> throw UnsupportedOperationException()
        }
    }
}
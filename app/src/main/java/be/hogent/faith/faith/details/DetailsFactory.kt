package be.hogent.faith.faith.details

import androidx.fragment.app.Fragment
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.drawing.view.ViewDrawingFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ViewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.details.text.view.ViewTextDetailFragment

enum class DetailType {
    PHOTO, DRAWING, AUDIO, TEXT
}

enum class DetailMetaDataType {
    EVENT, BACKPACK, CINEMA
}

object DetailsFactory {
    fun <T : Detail> createEditDetail(
        type: DetailType,
        strategy: DetailMetaDataType,
        detail: Detail?
    ): Fragment {
        return when (type) {
            DetailType.AUDIO -> RecordAudioFragment.newInstance(detail as AudioDetail)
            DetailType.TEXT -> TextDetailFragment.newInstance(detail as TextDetail)
            DetailType.DRAWING -> DrawingDetailFragment.newInstance(detail as DrawingDetail)
            DetailType.PHOTO -> TakePhotoFragment.newInstance(strategy)
        }
    }

    fun createViewDetail(detail: Detail): Fragment {
        return when (detail) {
            is AudioDetail -> RecordAudioFragment.newInstance(detail)
            is TextDetail -> ViewTextDetailFragment.newInstance(detail)
            is DrawingDetail -> ViewDrawingFragment.newInstance(detail)
            is PhotoDetail -> ViewPhotoFragment.newInstance(detail)
            // TODO aanvullen want dit geeft een fout
            else -> ViewPhotoFragment.newInstance(detail as PhotoDetail)
        }
    }

    fun createStrategy(type: DetailMetaDataType): DetailsMetaDataStrategy {
        return when (type) {
            DetailMetaDataType.BACKPACK -> BackpackStrategy()
            DetailMetaDataType.CINEMA -> CinemaStrategy()
            DetailMetaDataType.EVENT -> EventStrategy()
        }
    }
}
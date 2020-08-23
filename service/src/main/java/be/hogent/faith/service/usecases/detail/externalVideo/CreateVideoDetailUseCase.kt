package be.hogent.faith.service.usecases.detail.externalVideo

import android.media.ThumbnailUtils
import android.provider.MediaStore
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.util.ThumbnailProvider
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.io.File

class CreateVideoDetailUseCase(
    private val thumbnailProvider: ThumbnailProvider,
    observer: Scheduler
) : SingleUseCase<VideoDetail, CreateVideoDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<VideoDetail> {
        return Single.fromCallable {
            VideoDetail(params.tempSaveFile).apply {
                thumbnail = thumbnailProvider.getBase64EncodedThumbnail(
                    ThumbnailUtils.createVideoThumbnail(
                        file.path,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )
                )
            }
        }
    }

    class Params(
        val tempSaveFile: File
    )
}
package be.hogent.faith.service.usecases.detail.photoDetail

import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import be.hogent.faith.util.ThumbnailProvider
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class CreatePhotoDetailUseCase(
    private val thumbnailProvider: ThumbnailProvider,
    observer: Scheduler
) : SingleUseCase<PhotoDetail, CreatePhotoDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<PhotoDetail> {
        return Single.fromCallable {
            PhotoDetail(
                file = params.tempPhotoSaveFile,
                thumbnail = thumbnailProvider.getBase64EncodedThumbnail(params.tempPhotoSaveFile)
            )
        }
    }

    class Params(
        val tempPhotoSaveFile: File
    )
}
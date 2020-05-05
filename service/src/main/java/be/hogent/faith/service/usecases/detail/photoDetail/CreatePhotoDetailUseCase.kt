package be.hogent.faith.service.usecases.detail.photoDetail

import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class CreatePhotoDetailUseCase(
    observer: Scheduler
) : SingleUseCase<PhotoDetail, CreatePhotoDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<PhotoDetail> {
        return Single.fromCallable { PhotoDetail(params.tempPhotoSaveFile) }
    }

    class Params(
        val tempPhotoSaveFile: File
    )
}
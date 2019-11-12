package be.hogent.faith.service.usecases.photoDetail

import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class CreatePhotoDetailUseCase(
    observeScheduler: Scheduler
) : SingleUseCase<PhotoDetail, CreatePhotoDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<PhotoDetail> {
        return Single.fromCallable { PhotoDetail(params.tempPhotoSaveFile) }
    }

    class Params(
        val tempPhotoSaveFile: File
    )
}
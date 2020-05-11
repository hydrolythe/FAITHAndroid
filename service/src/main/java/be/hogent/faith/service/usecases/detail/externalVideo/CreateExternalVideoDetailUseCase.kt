package be.hogent.faith.service.usecases.detail.externalVideo

import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class CreateExternalVideoDetailUseCase(
    observer: Scheduler
) : SingleUseCase<ExternalVideoDetail, CreateExternalVideoDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<ExternalVideoDetail> {
        return Single.fromCallable { ExternalVideoDetail(params.tempExternalVideoSaveFile) }
    }

    class Params(
        val tempExternalVideoSaveFile: File
    )
}
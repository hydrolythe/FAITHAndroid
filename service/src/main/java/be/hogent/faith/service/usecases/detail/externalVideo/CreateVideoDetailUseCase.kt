package be.hogent.faith.service.usecases.detail.externalVideo

import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import java.io.File

class CreateVideoDetailUseCase(
    observer: Scheduler
) : SingleUseCase<VideoDetail, CreateVideoDetailUseCase.Params>(observer) {

    override fun buildUseCaseSingle(params: Params): Single<VideoDetail> {
        return Single.fromCallable { VideoDetail(params.tempSaveFile) }
    }

    class Params(
        val tempSaveFile: File
    )
}
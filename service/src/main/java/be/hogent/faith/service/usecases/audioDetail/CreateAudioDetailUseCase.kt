package be.hogent.faith.service.usecases.audioDetail

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.service.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.File

class CreateAudioDetailUseCase(
    observeScheduler: Scheduler
) : SingleUseCase<AudioDetail, CreateAudioDetailUseCase.Params>(observeScheduler) {

    override fun buildUseCaseSingle(params: Params): Single<AudioDetail> {
        return Single.fromCallable { AudioDetail(params.tempAudioSaveFile) }
    }

    class Params(
        val tempAudioSaveFile: File
    )
}
package be.hogent.faith.service.usecases.cinema

import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.service.usecases.base.ObservableUseCase
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Params
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Status
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber

class CreateCinemaVideoUseCase(
    private val videoEncoder: VideoEncoder,
    observer: Scheduler
) : ObservableUseCase<Status, Params>(
    observer
) {
    class Params(
        val details: List<Detail>,
        val resolution: VideoEncoder.Resolution
    )

    sealed class Status {
        class InProgress(val progress: Int) : Status()
        class Completed(val videoDetail: FilmDetail) : Status()
    }

    override fun buildUseCaseObservable(params: Params): Observable<Status> {
        return Observable.create { emitter ->
            val encoderListener = object : VideoEncoder.EncodingProgressListener {
                override fun onProgressChanged(progress: Int) {
                    emitter.onNext(Status.InProgress(progress))
                }
            }
            val resultingFile = videoEncoder.encode(
                params.details.map(Detail::file),
                params.resolution,
                encoderListener
            )
            emitter.onNext(Status.Completed(FilmDetail(resultingFile)))
            emitter.onComplete()
        }
    }
}
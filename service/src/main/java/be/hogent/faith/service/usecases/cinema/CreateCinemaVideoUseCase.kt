package be.hogent.faith.service.usecases.cinema

import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.service.usecases.base.ObservableUseCase
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Params
import be.hogent.faith.service.usecases.cinema.CreateCinemaVideoUseCase.Status
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler
import timber.log.Timber

class CreateCinemaVideoUseCase(
    private val videoEncoder: VideoEncoder,
    private val loadFileUseCase: LoadDetailFileUseCase<Cinema>,
    observer: Scheduler
) : ObservableUseCase<Status, Params>(
    observer
) {
    class Params(
        val details: List<Detail>,
        val cinema: Cinema,
        val resolution: VideoEncoder.Resolution
    )

    sealed class Status {
        class InProgress(val progress: Int) : Status()
        class Completed(val videoDetail: FilmDetail) : Status()
    }

    override fun buildUseCaseObservable(params: Params): Observable<Status> {
        return Observable.fromIterable(params.details)
            .flatMapCompletable { detail ->
                loadFileUseCase.buildUseCaseObservable(
                    LoadDetailFileUseCase.Params(detail, params.cinema)
                ).doOnComplete {
                    Timber.i("Downloaded one of the details for the videoEncoder")
                }
            }.andThen(
                Observable.create { emitter ->
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
            )
    }
}
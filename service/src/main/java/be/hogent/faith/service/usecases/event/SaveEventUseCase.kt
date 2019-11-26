package be.hogent.faith.service.usecases.event

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.EventRepository
import be.hogent.faith.service.usecases.base.CompletableUseCase
import be.hogent.faith.storage.IStorageRepository
import io.reactivex.Completable
import io.reactivex.Scheduler

open class SaveEventUseCase(
    private val eventRepository: EventRepository,
    private val storageRepository: IStorageRepository,
    observeScheduler: Scheduler
) : CompletableUseCase<SaveEventUseCase.Params>(observeScheduler) {

    // private val filesToRemove = mutableListOf<File>()
    private var params: Params? = null

    override fun buildUseCaseObservable(params: Params): Completable {
        this.params = params
        return addEventToUser(params.event)
            .concatWith(
                Completable.fromMaybe(eventRepository.insert(params.event, params.user))
                    .concatWith(Completable.fromSingle(storageRepository.saveEvent(params.event)))
            )
    }

    fun addEventToUser(event: Event): Completable = Completable.fromCallable {
        event.title = params!!.eventTitle
        // First add in domain so we can do business logic
        // If this fails the event won't get added to the Repo.
        params!!.user.addEvent(event)
    }

    /*
     fun addEventToUser(event: Event): Single<Event> = Single.fromCallable {
         event.title = params!!.eventTitle
         // First add in domain so we can do business logic
         // If this fails the event won't get added to the Repo.
         params!!.user.addEvent(event)
         event
     }

     fun saveEmotionAvatar(event: Event): Single<Event> =
         storageRepository.saveEventEmotionAvatar(event)
             .doOnSuccess {
                 event.emotionAvatar?.let { filesToRemove.add(it) }
             }
             .map {
                 event.emotionAvatar = it
                 event
             }

     fun saveDetailFiles(event: Event): Single<Event> =
         event.details.toFlowable()
             .concatMapSingle { detail ->
                 filesToRemove.add(detail.file)
                 storageRepository.saveDetailFile(event, detail).map { file ->
                     event.getDetail(detail.uuid)!!.file = file
                     event
                 }
             }
             .last(event)

     fun saveEvent(event: Event): Single<Event> = eventRepository.insert(event, params!!.user)
         .doOnSuccess {
             params!!.event = it
         }.toSingle()

     fun deleteLocalFiles(event: Event): Single<Event> =
         Single.fromCallable {
             filesToRemove.forEach {
                 storageRepository.deleteFile(it)
             }
         }.map { event }
 */
    data class Params(
        val eventTitle: String,
        var event: Event,
        val user: User
    )
}

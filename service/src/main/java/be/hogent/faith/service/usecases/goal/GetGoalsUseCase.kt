package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class GetGoalsUseCase(
    private val goalRepository: IGoalRepository,
    private val goalEncryptionService: IGoalEncryptionService,
    observer: Scheduler,
    subscriber: Scheduler = Schedulers.io()
) : FlowableUseCase<List<Goal>, GetGoalsUseCase.Params>(observer, subscriber) {

    override fun buildUseCaseObservable(params: Params): Flowable<List<Goal>> {
        return goalRepository.getAll()
            .subscribeOn(subscriber)
            .doOnEach { Timber.i("Got encrypted goals") }
            .doOnError {
                Timber.e("Error while fetching encrypted goals: ${it.localizedMessage}")
                it.printStackTrace()
            }
            .concatMapSingle { list ->
                Observable.fromIterable(list)
                    .flatMapSingle { encryptedGoal ->
                        goalEncryptionService.decryptData(encryptedGoal)
                            .subscribeOn(subscriber)
                            .doOnSuccess { Timber.i("Decrypted goal ${encryptedGoal.uuid}") }
                            .doOnError {
                                Timber.e("Error while decrypting goal: ${it.localizedMessage}")
                                it.printStackTrace()
                            }
                    }
                    .toList()
                    .doOnSuccess {
                        params.user.setGoals(it)
                        Timber.i("Decrypted goals for user ${params.user.username}") }
            }
    }

    data class Params(
        val user: User
    )
}
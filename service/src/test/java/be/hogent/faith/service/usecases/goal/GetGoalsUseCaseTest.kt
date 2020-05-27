package be.hogent.faith.service.usecases.goal

import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.util.EncryptedGoalFactory
import be.hogent.faith.util.factory.GoalFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

class GetGoalsUseCaseTest {
    private lateinit var getgoalsUC: GetGoalsUseCase
    private val goalEncryptionService = mockk<IGoalEncryptionService>()
    private val goalRepository = mockk<IGoalRepository>()

    @Before
    fun setUp() {
        getgoalsUC = GetGoalsUseCase(goalRepository, goalEncryptionService, mockk(), Schedulers.trampoline())
    }

    @Test
    fun `getting goals calls the repository`() {
        every { goalRepository.getAll(true) } returns Flowable.just(
            EncryptedGoalFactory.makeGoalList(2),
            EncryptedGoalFactory.makeGoalList(2)
        )
        val params = GetGoalsUseCase.Params(UserFactory.makeUser(), true)

        getgoalsUC.buildUseCaseObservable(params)
            .test()

        verify { goalRepository.getAll(true) }
    }

    @Test
    fun `getting all goals, given goals are present, returns all goals`() {
        val params = GetGoalsUseCase.Params(UserFactory.makeUser(), false)
        // Simulate two list of goals on the stream
        every { goalRepository.getAll(false) } returns Flowable.just(
            EncryptedGoalFactory.makeGoalList(2),
            EncryptedGoalFactory.makeGoalList(2)
        )
        every { goalEncryptionService.decryptData(any()) } returns Single.defer { // Defer to ensure a new goal is made with each call
            Single.just(GoalFactory.makeGoal())
        }
        getgoalsUC.buildUseCaseObservable(params)
            .test()
            .assertValueCount(2)
    }

    @Test
    fun `getting all active goals, given active goals are present, returns all active goals`() {
        val params = GetGoalsUseCase.Params(UserFactory.makeUser(), true)
        // Simulate two list of goals on the stream
        every { goalRepository.getAll(true) } returns Flowable.just(
            EncryptedGoalFactory.makeGoalList(2),
            EncryptedGoalFactory.makeGoalList(2)
        )
        every { goalEncryptionService.decryptData(any()) } returns Single.defer { // Defer to ensure a new goal is made with each call
            Single.just(GoalFactory.makeGoal())
        }
        getgoalsUC.buildUseCaseObservable(params)
            .test()
            .assertValueCount(2)
    }

    @Test
    fun `getting all goals, no goals present, returns nothing`() {
        val params = GetGoalsUseCase.Params(UserFactory.makeUser(), false)
        every { goalRepository.getAll(any()) } returns Flowable.empty()
        getgoalsUC.buildUseCaseObservable(params).test().assertNoValues()
    }
}

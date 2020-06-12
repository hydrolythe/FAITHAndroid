package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.encryption.IGoalEncryptionService
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.service.usecases.util.EncryptedGoalFactory
import be.hogent.faith.util.factory.GoalFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class UpdateGoalUseCaseTest {
    private val goalEncryptionService: IGoalEncryptionService = mockk(relaxed = true)
    private val goalRepository: IGoalRepository = mockk(relaxed = true)
    private lateinit var updateGoalUseCase: UpdateGoalUseCase

    private lateinit var goal: Goal
    private val encryptedGoal = EncryptedGoalFactory.makeGoal()

    @Before
    fun setUp() {
        goal = GoalFactory.makeGoal()
        updateGoalUseCase =
            UpdateGoalUseCase(
                goalEncryptionService,
                goalRepository,
                mockk()
            )
        every { goalEncryptionService.encrypt(goal) } returns Single.just(encryptedGoal)
        every { goalRepository.update(encryptedGoal) } returns Completable.complete()
    }

    @Test
    fun `updating the Goal should complete without errors`() {
        val params = UpdateGoalUseCase.Params(goal)
        every { goalEncryptionService.encrypt(any()) } returns Single.just(encryptedGoal)
        every { goalRepository.update(any()) } returns Completable.complete()

        val result = updateGoalUseCase.buildUseCaseObservable(params)

        result.test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { goalRepository.update(encryptedGoal) }
    }

    @Test
    fun `When an error occurs in the GoalRepository it returns an Error`() {
        // Arrange
        every { goalEncryptionService.encrypt(any()) } returns Single.just(encryptedGoal)
        every { goalRepository.update(any()) } returns Completable.error(RuntimeException())

        val params = UpdateGoalUseCase.Params(goal)

        updateGoalUseCase.buildUseCaseObservable(params)
            .test()
            .assertError(RuntimeException::class.java)
    }
}
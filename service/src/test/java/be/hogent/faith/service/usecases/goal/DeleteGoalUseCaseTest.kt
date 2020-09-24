package be.hogent.faith.service.usecases.goal

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.service.repositories.IGoalRepository
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import org.junit.Before
import org.junit.Test
import java.lang.RuntimeException

class DeleteGoalUseCaseTest {
    private val goalRepository = mockk<IGoalRepository>()
    private lateinit var deleteGoalUC: DeleteGoalUseCase
    private val goal = mockk<Goal>()
    private val user = UserFactory.makeUser()

    @Before
    fun setUp() {
        deleteGoalUC = DeleteGoalUseCase(goalRepository, mockk())
    }

    @Test
    fun `deleting a goal returns Completable on success`() {
        // Arrange
        val params = DeleteGoalUseCase.Params(goal, user)
        every { goalRepository.delete(goal.uuid) } returns Completable.complete()

        // Act
        deleteGoalUC.buildUseCaseObservable(params)
            .test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { goalRepository.delete(goal.uuid) }
    }

    @Test
    fun `deleting a goal removes it from the user`() {
        // Arrange
        // TODO user.addGoal(goal)
        val params = DeleteGoalUseCase.Params(goal, user)
        every { goalRepository.delete(goal.uuid) } returns Completable.complete()

        // Act
        deleteGoalUC.buildUseCaseObservable(params)
            .test()
            .dispose()

        // Assert
        // TODO   Assert.assertFalse(user.goals.contains(goal))
    }

    @Test
    fun `deleting a goal when an error occurs in the goalRepository returns an error`() {
        val params = DeleteGoalUseCase.Params(goal, user)
        every { goalRepository.delete(goal.uuid) } returns Completable.error(RuntimeException())

        // Act
        deleteGoalUC.buildUseCaseObservable(params)
            .test()
            .assertError(RuntimeException::class.java)
    }
}

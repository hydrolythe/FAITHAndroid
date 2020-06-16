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
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test

class SaveGoalUseCaseTest {
    private val goalEncryptionService: IGoalEncryptionService = mockk(relaxed = true)
    private val goalRepository: IGoalRepository = mockk(relaxed = true)
    private lateinit var saveGoalUseCase: SaveGoalUseCase

    private lateinit var goal: Goal
    private lateinit var user: User

    private val encryptedGoal = EncryptedGoalFactory.makeGoal()

    @Before
    fun setUp() {
        goal = GoalFactory.makeGoal()
        user = UserFactory.makeUser(numberOfGoals = 0)
        saveGoalUseCase =
            SaveGoalUseCase(
                goalEncryptionService,
                goalRepository,
                mockk()
            )
        every { goalEncryptionService.encrypt(goal) } returns Single.just(encryptedGoal)
        every { goalRepository.insert(encryptedGoal) } returns Completable.complete()
    }

    @Test
    fun `saving the Goal should complete without errors`() {
        val params = SaveGoalUseCase.Params(goal, user)

        val result = saveGoalUseCase.buildUseCaseObservable(params)

        result.test()
            .assertNoErrors()
            .assertComplete()

        // Assert
        verify { goalRepository.insert(encryptedGoal) }
    }

    @Test
    fun `saving the Goal it should call the repository`() {
        val params = SaveGoalUseCase.Params(goal, user)

        val result = saveGoalUseCase.buildUseCaseObservable(params)

        result.test()
            .dispose()

        // TODO    Assert.assertTrue(user.Goals.contains(goal))
    }

    @Test
    fun `After saving the Goal it should be in the user's list of Goals`() {
        val params = SaveGoalUseCase.Params(goal, user)

        val result = saveGoalUseCase.buildUseCaseObservable(params)

        result.test()
            .dispose()

        // TODO    Assert.assertTrue(user.Goals.contains(goal))
    }

    @Test
    fun `When an error occurs in the GoalRepository it returns an Error`() {
        // Arrange
        every { goalRepository.insert(any()) } returns Completable.error(RuntimeException())

        val params = SaveGoalUseCase.Params(goal, user)

        saveGoalUseCase.buildUseCaseObservable(params)
            .test()
            .assertError(RuntimeException::class.java)
    }

    @Test
    fun `When an error occurs in the GoalRepository the Goal is not added to the user's Goals`() {
        every { goalRepository.insert(any()) } returns Completable.error(RuntimeException())

        val params = SaveGoalUseCase.Params(goal, user)

        val result = saveGoalUseCase.buildUseCaseObservable(params)
        result.test()

        // TODO      Assert.assertFalse(user.Goals.contains(goal))
    }
}
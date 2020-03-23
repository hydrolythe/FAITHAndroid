package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.repository.BackpackRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackTextDetailUseCaseTest {

    private lateinit var saveBackpackTextDetailUseCase: SaveBackpackTextDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: BackpackRepository = mockk(relaxed = true)
    private val user: User = mockk(relaxed = true)

    private val detail = mockk<TextDetail>()

    @Before
    fun setUp() {
        saveBackpackTextDetailUseCase =
            SaveBackpackTextDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveTextUC_saveTextNormal_savedToStorage() {
        // Arrange
        every { repository.insertDetail(detail, user) } returns Maybe.fromSingle { detail }
        val params = SaveBackpackTextDetailUseCase.Params(user, detail)

        // Act
        saveBackpackTextDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()

        // Assert
        verify { repository.insertDetail(detail, user) }
    }
}
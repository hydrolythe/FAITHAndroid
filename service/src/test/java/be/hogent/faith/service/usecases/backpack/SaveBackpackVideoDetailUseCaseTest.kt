package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.domain.repository.DetailContainerRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackVideoDetailUseCaseTest {
    private lateinit var saveBackpackVideoDetailUseCase: SaveYoutubeDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: DetailContainerRepository<YoutubeVideoDetail> = mockk(relaxed = true)
    private val user: User = mockk(relaxed = true)

    private val detail = mockk<YoutubeVideoDetail>()

    @Before
    fun setUp() {
        saveBackpackVideoDetailUseCase =
            SaveYoutubeDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveVideoUC_saveVideoNormal_savedToStorage() {
        // Arrange
        every { repository.insertDetail(detail, user) } returns Maybe.fromSingle { detail }
        val params = SaveYoutubeDetailUseCase.Params(user, detail)

        // Act
        saveBackpackVideoDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()

        // Assert
        verify { repository.insertDetail(detail, user) }
    }
}

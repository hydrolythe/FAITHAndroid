package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.repository.IBackpackRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Maybe
import io.reactivex.Scheduler
import org.junit.Before
import org.junit.Test

class SaveBackpackAudioDetailUseCaseTest {
    private lateinit var saveBackpackAudioDetailUseCase: SaveBackpackAudioDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: IBackpackRepository = mockk(relaxed = true)
    private val user: User = mockk(relaxed = true)

    private val detail = mockk<AudioDetail>()

    @Before
    fun setUp() {
        saveBackpackAudioDetailUseCase =
            SaveBackpackAudioDetailUseCase(
                repository,
                scheduler
            )
    }

    @Test
    fun saveAudioUC_saveAudioNormal_savedToStorage() {
        // Arrange
        every { repository.insertDetail(detail, user) } returns Maybe.fromSingle { detail }
        val params = SaveBackpackAudioDetailUseCase.Params(user, detail)

        // Act
        saveBackpackAudioDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()

        // Assert
        verify { repository.insertDetail(detail, user) }
    }
}
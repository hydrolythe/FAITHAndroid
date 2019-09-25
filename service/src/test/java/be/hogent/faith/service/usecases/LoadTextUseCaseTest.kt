package be.hogent.faith.service.usecases

import be.hogent.faith.storage.StorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.io.File

class LoadTextUseCaseTest {
    private lateinit var loadTextUseCase: LoadTextDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: StorageRepository = mockk(relaxed = true)

    private val text = "<font size='5'>Hello <b>World</b></font>"
    private val saveFile = mockk<File>()

    @Before
    fun setUp() {
        loadTextUseCase = LoadTextDetailUseCase(repository, scheduler)
    }

    @Test
    fun loadTextUC_normal_returnsTextFromFile() {
        // Arrange
        every { repository.loadTextFromExistingDetail(saveFile) } returns Single.just(text)

        // Act
        loadTextUseCase.buildUseCaseSingle(
            LoadTextDetailUseCase.LoadTextParams(saveFile)
        ).test()
            .assertValue(text)

        // Assert
        verify { repository.loadTextFromExistingDetail(saveFile) }
    }

    @Test
    fun loadTextUC_repoThrowsError_passesError() {
        // Arrange
        every { repository.loadTextFromExistingDetail(saveFile) } returns Single.error(RuntimeException())

        // Act & Assert
        loadTextUseCase.buildUseCaseSingle(
            LoadTextDetailUseCase.LoadTextParams(saveFile)
        ).test()
            .assertError(RuntimeException::class.java)
    }
}
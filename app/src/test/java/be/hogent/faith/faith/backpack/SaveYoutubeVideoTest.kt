package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveYoutubeDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class SaveYoutubeVideoTest {
    private lateinit var viewModel: BackpackViewModel
    private val saveVideoUseCase = mockk<SaveYoutubeDetailUseCase>(relaxed = true)
    private lateinit var detail: YoutubeVideoDetail
    private val user: User = mockk()
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            mockk(),
            mockk(),
            mockk(),
            getBackPackFilesDummyUseCase,
            saveVideoUseCase
        )

        detail = YoutubeVideoDetail(File(""), "Video", videoId = "")
    }

    @Test
    fun youtubeViewModel_saveVideo_callsUseCase() {
        val params = slot<SaveYoutubeDetailUseCase.Params>()

        viewModel.saveYoutubeDetail(user, detail)
        verify { saveVideoUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.youtubeDetail)
    }

    @Test
    fun youtubeViewModel_saveVideo_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.saveYoutubeDetail(user, detail)
        verify { saveVideoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { errorObserver wasNot called }
    }

    @Test
    fun youtubeViewModel_saveVideo_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Detail>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.saveYoutubeDetail(user, detail)
        verify { saveVideoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}
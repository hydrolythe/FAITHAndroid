package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.backpackScreen.youtubeVideo.create.YoutubeVideoDetailViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackVideoDetailUseCase
import io.mockk.Called
import io.mockk.called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableCompletableObserver
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.UUID

class SaveYoutubeVideoTest {
    private lateinit var viewModel: YoutubeVideoDetailViewModel
    private val saveVideoUseCase = mockk<SaveBackpackVideoDetailUseCase>(relaxed = true)
    private lateinit var detail : YoutubeVideoDetail
    private val user: User = mockk()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = YoutubeVideoDetailViewModel(
            mockk(),
            saveVideoUseCase
        )

        detail = YoutubeVideoDetail(File(""), "Video", videoId = "")
    }

    @Test
    fun youtubeViewModel_saveVideo_callsUseCase() {
        val params = slot<SaveBackpackVideoDetailUseCase.Params>()

        viewModel.saveVideoDetail(detail, user)
        verify { saveVideoUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.videoDetail)
    }

    @Test
    fun youtubeViewModel_saveVideo_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.saveVideoDetail(detail, user)
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
        viewModel.videoIsSaved.observeForever(successObserver)

        // Act
        viewModel.saveVideoDetail(detail, user)
        verify { saveVideoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}
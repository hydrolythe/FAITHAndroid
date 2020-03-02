package be.hogent.faith.faith.backpack

import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.Called
import io.mockk.called
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.reactivex.observers.DisposableCompletableObserver
import org.junit.Assert.assertEquals
import androidx.lifecycle.Observer
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import be.hogent.faith.service.usecases.backpack.SaveBackpackPhotoDetailUseCase

class BackpackViewModelSavePhotoTest {
    private lateinit var viewModel: BackpackViewModel
    private val savePhotoUseCase = mockk<SaveBackpackPhotoDetailUseCase>(relaxed = true)
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>(relaxed = true)
    private val detail = mockk<PhotoDetail>()

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            mockk(),
            mockk(),
            savePhotoUseCase,
            mockk(),
            getBackPackFilesDummyUseCase
        )
    }

    @Test
    fun backpackViewModel_savePhoto_callsUseCase() {
        val params = slot<SaveBackpackPhotoDetailUseCase.Params>()

        viewModel.savePhotoDetail(detail)
        verify { savePhotoUseCase.execute(capture(params), any()) }

        assertEquals(detail, params.captured.photoDetail)
    }

    @Test
    fun backpackViewModel_savePhoto_notifiesUseCaseSuccess() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.photoDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.savePhotoDetail(detail)
        verify { savePhotoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onComplete()

        // Assert
        verify { successObserver.onChanged(R.string.save_photo_success) }
        verify { errorObserver wasNot called }
    }

    @Test
    fun backpackViewModel_savePhoto_notifiesUseCaseFailure() {
        // Arrange
        val useCaseObserver = slot<DisposableCompletableObserver>()
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val successObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)
        viewModel.photoDetailSavedSuccessFully.observeForever(successObserver)

        // Act
        viewModel.savePhotoDetail(detail)
        verify { savePhotoUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk(relaxed = true))

        // Assert
        verify { errorObserver.onChanged(any()) }
        verify { successObserver wasNot Called }
    }
}
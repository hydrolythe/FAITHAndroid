package be.hogent.faith.faith.details.photo.create

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.usecases.detail.photoDetail.CreatePhotoDetailUseCase
import io.mockk.Called
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.observers.DisposableSingleObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class TakePhotoViewModelUseCaseTests {
    private lateinit var viewModel: TakePhotoViewModel
    private val createPhotoDetailUseCase = mockk<CreatePhotoDetailUseCase>(relaxed = true)

    @get:Rule
    val testRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        viewModel = TakePhotoViewModel(createPhotoDetailUseCase)
    }

    @Test
    fun takePhotoViewModel_onSaveClicked_callsUseCase() {
        // Arrange
        // We need to save a picture before we can call the use case
        viewModel.photoTaken(mockk())

        // Act
        viewModel.onSaveClicked()
        verify { createPhotoDetailUseCase.execute(any(), any()) }
    }

    @Test
    fun takePhotoViewModel_onSaveClicked_updatesDetailWhenUseCaseSucceeds() {
        // Arrange
        val getDetailMetaDataObserver = mockk<Observer<Unit>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val useCaseObserver = slot<DisposableSingleObserver<PhotoDetail>>()
        val useCaseParams = slot<CreatePhotoDetailUseCase.Params>()
        val createdDetail = mockk<PhotoDetail>()
        // We need to save a picture before we can call the use case
        val photoSaveFile = mockk<File>()
        viewModel.photoTaken(photoSaveFile)

        viewModel.getDetailMetaData.observeForever(getDetailMetaDataObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify {
            createPhotoDetailUseCase.execute(
                capture(useCaseParams),
                capture(useCaseObserver)
            )
        }
        useCaseObserver.captured.onSuccess(createdDetail)

        // Assert
        assertEquals(photoSaveFile, useCaseParams.captured.tempPhotoSaveFile)
        verify { getDetailMetaDataObserver.onChanged(any()) }
        verify { errorObserver wasNot Called }
    }

    @Test
    fun takePhotoViewModel_onSaveClicked_errorMessageWhenUseCaseFails() {
        // Arrange
        val detailObserver = mockk<Observer<PhotoDetail>>(relaxed = true)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        val useCaseObserver = slot<DisposableSingleObserver<PhotoDetail>>()
        // We need to save a picture before we can call the use case
        viewModel.photoTaken(mockk())

        viewModel.savedDetail.observeForever(detailObserver)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked()
        verify { createPhotoDetailUseCase.execute(any(), capture(useCaseObserver)) }
        useCaseObserver.captured.onError(mockk())

        // Assert
        verify { errorObserver.onChanged(R.string.create_photo_failed) }
        verify { detailObserver wasNot Called }
    }
}
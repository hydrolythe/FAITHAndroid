package be.hogent.faith.faith.details.drawing.create

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.service.usecases.drawingDetail.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.drawingDetail.OverwriteDrawingDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

class DrawingDetailViewModel(
    private val createDrawingDetailUseCase: CreateDrawingDetailUseCase,
    private val overwriteDrawingDetailUseCase: OverwriteDrawingDetailUseCase
) : DrawViewModel(), DetailViewModel<DrawingDetail> {

    private val _savedDetail = MutableLiveData<DrawingDetail>()
    override val savedDetail: LiveData<DrawingDetail> = _savedDetail

    private var existingDetail: DrawingDetail? = null

    init {
        existingDetail?.let {
            loadExistingDetail(it)
        }
        _drawingActions.value = mutableListOf()
        _selectedColor.value = Color.BLACK
        _selectedLineWidth.value = LineWidth.MEDIUM
    }

    override fun loadExistingDetail(existingDetail: DrawingDetail) {
        this.existingDetail = existingDetail
        // The approach in the TextViewModel of fetching the existing text  using the UC
        // and setting it in the VM is not applicable here because we have to interact
        // directly with an Android-element (DrawView). Instead setting up the DrawView is done in the
        // [DrawingDetailFragment].
    }

    fun onBitMapAvailable(bitmap: Bitmap) {
        if (existingDetail != null) {
            val params = OverwriteDrawingDetailUseCase.Params(bitmap, existingDetail!!)
            overwriteDrawingDetailUseCase.execute(params, OverwriteDrawingDetailUseCaseHandler())
        } else {
            val params = CreateDrawingDetailUseCase.Params(bitmap)
            createDrawingDetailUseCase.execute(params, CreateDrawingDetailUseCaseHandler())
        }
    }

    private inner class OverwriteDrawingDetailUseCaseHandler :
        DisposableCompletableObserver() {
        override fun onComplete() {
            Timber.i("Successfully overwrote existing existingDetail $existingDetail with new bitmap")
            _savedDetail.value = existingDetail
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_drawing_failed)
            Timber.e(e)
        }
    }

    private inner class CreateDrawingDetailUseCaseHandler :
        DisposableSingleObserver<DrawingDetail>() {
        override fun onSuccess(createdDetail: DrawingDetail) {
            _savedDetail.value = createdDetail
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.error_save_drawing_failed)
            Timber.e(e)
        }
    }
}

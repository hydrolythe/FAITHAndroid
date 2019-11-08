package be.hogent.faith.faith.details.drawing.create

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.service.usecases.CreateDrawingDetailUseCase
import be.hogent.faith.service.usecases.OverwriteDrawingDetailUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

class DrawingDetailViewModel(
    existingDetail: DrawingDetail? = null,
    private val createDrawingDetailUseCase: CreateDrawingDetailUseCase,
    private val overwriteDrawingDetailUseCase: OverwriteDrawingDetailUseCase
) : DrawViewModel(), DetailViewModel<DrawingDetail> {

    private val _savedDetail = MutableLiveData<DrawingDetail>()
    override val savedDetail: LiveData<DrawingDetail> = _savedDetail

    private var detail: DrawingDetail? = null

    init {
        existingDetail?.let {
            loadExistingDetail(it)
        }
        _drawingActions.value = mutableListOf()
        _selectedColor.value = Color.BLACK
        _selectedLineWidth.value = LineWidth.MEDIUM
    }

    override fun loadExistingDetail(existingDetail: DrawingDetail) {
        detail = existingDetail
        // The approach in the TextViewModel of fetching the existing text  using the UC
        // and setting it in the VM is not applicable here because we have to interact
        // directly with an Android-element (DrawView). Instead setting up the DrawView is done in the
        // [MakeDrawingFragment].
    }

    fun onBitMapAvailable(bitmap: Bitmap) {
        if (detail != null) {
            val params = OverwriteDrawingDetailUseCase.Params(bitmap, detail!!)
            overwriteDrawingDetailUseCase.execute(params, OverwriteDrawingDetailUseCaseHandler())
        } else {
            val params = CreateDrawingDetailUseCase.Params(bitmap)
            createDrawingDetailUseCase.execute(params, CreateDrawingDetailUseCaseHandler())
        }
    }

    private inner class OverwriteDrawingDetailUseCaseHandler :
        DisposableCompletableObserver() {
        override fun onComplete() {
            Timber.i("Successfully overwrote existing detail $detail with new bitmap")
            _savedDetail.value = detail
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

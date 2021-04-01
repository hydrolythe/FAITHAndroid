package be.hogent.faith.faith.details.drawing.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.R
import be.hogent.faith.faith.details.DetailViewModel
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.retrofitmodels.OverwritableImageDetail
import be.hogent.faith.faith.util.SingleLiveEvent
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class DrawingDetailViewModel(
    val drawingDetailRepository: IDrawingDetailRepository
) : DrawViewModel(), DetailViewModel<DrawingDetail> {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _savedDetail = MutableLiveData<DrawingDetail>()
    override val savedDetail: LiveData<DrawingDetail> = _savedDetail

    private val _getDetailMetaData = SingleLiveEvent<Unit>()
    override val getDetailMetaData: LiveData<Unit> = _getDetailMetaData

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

    fun onBitMapAvailable(context: Context, bitmap: Bitmap) {
        val saveDirectory = File(context.cacheDir, "pictures")
        saveDirectory.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        uiScope.launch {
            val result = drawingDetailRepository.createNewDetail(saveDirectory)
            if (result.success != null) {
                existingDetail = result.success
                _getDetailMetaData.call()
            }
            if (result.exception != null) {
                _errorMessage.postValue(R.string.error_save_drawing_failed)
                Timber.e(result.exception)
            }
        }
    }

    override fun setDetailsMetaData(title: String, dateTime: LocalDateTime) {
        existingDetail?.let {
            it.title = title
            it.dateTime = dateTime
        }
        _savedDetail.value = existingDetail
    }
}

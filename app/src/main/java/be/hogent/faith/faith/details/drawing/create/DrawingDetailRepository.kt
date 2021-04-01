package be.hogent.faith.faith.details.drawing.create

import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.RetrofitRequestAdapter
import be.hogent.faith.faith.details.audio.AudioDetailResult
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import be.hogent.faith.faith.models.retrofitmodels.OverwritableImageDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class DrawingDetailRepository:IDrawingDetailRepository {
    override suspend fun createNewDetail(file:File): DrawingDetailResult {
        return withContext(Dispatchers.IO){
            val part = MultipartBody.Part.createFormData(
                "file", file.name, RequestBody.create(
                    MediaType.parse("image/png"),
                    file.readBytes()
                )
            )
            val request = RetrofitAdapter.drawingDetailApiService.getDrawingDetail(part)
            try {
                val result = request.execute()
                DrawingDetailResult(success=result.body())
            }
            catch(e:Exception){
                DrawingDetailResult(exception=e)
            }
        }
    }

    override suspend fun overwriteDetail(imageDetail: OverwritableImageDetail): DrawingDetailResult {
        return withContext(Dispatchers.IO){
            val request = RetrofitAdapter.drawingDetailApiService.overwriteDetail(imageDetail)
            try {
                val result = request.execute()
                DrawingDetailResult(success=result.body())
            }
            catch(e:Exception){
                DrawingDetailResult(exception=e)
            }
        }
    }

}
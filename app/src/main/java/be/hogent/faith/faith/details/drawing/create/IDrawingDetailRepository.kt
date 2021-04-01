package be.hogent.faith.faith.details.drawing.create

import be.hogent.faith.faith.details.audio.AudioDetailResult
import be.hogent.faith.faith.models.retrofitmodels.OverwritableImageDetail
import okhttp3.RequestBody
import java.io.File

interface IDrawingDetailRepository {
    suspend fun createNewDetail(file:File):DrawingDetailResult
    suspend fun overwriteDetail(imageDetail: OverwritableImageDetail):DrawingDetailResult
}
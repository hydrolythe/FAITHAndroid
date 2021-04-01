package be.hogent.faith.faith.models.retrofitmodels

import be.hogent.faith.faith.models.detail.DrawingDetail
import java.io.File

data class OverwritableImageDetail(val detail: DrawingDetail, val imageFile: File)

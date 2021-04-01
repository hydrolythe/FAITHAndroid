package be.hogent.faith.faith.details.drawing.create

import be.hogent.faith.faith.models.detail.DrawingDetail

data class DrawingDetailResult(val success: DrawingDetail?=null, val exception:Exception?=null)

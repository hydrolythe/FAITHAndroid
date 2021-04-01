package be.hogent.faith.faith.details.text.create

import be.hogent.faith.faith.models.detail.TextDetail

data class TextDetailResult(val success: TextDetail?=null, val exception:Exception?=null)

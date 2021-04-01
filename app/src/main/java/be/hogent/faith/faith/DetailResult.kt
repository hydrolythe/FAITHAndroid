package be.hogent.faith.faith

import be.hogent.faith.faith.models.detail.Detail

data class DetailResult(var success: Detail?=null, val exception:Exception?=null)

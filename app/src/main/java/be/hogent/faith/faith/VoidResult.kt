package be.hogent.faith.faith

import okhttp3.ResponseBody

data class VoidResult(val success: ResponseBody?=null, val exception:Exception?=null)

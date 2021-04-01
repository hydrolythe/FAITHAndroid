package be.hogent.faith.faith.details.photo.create

import be.hogent.faith.faith.models.detail.PhotoDetail

data class PhotoResult(val success:PhotoDetail?=null,val exception:Exception?=null)

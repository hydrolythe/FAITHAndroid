package be.hogent.faith.faith.models

import android.os.Parcelable
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailArray(val details:List<ExpandedDetail>):Parcelable

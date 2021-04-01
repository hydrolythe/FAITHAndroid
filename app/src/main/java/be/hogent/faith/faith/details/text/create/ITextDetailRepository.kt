package be.hogent.faith.faith.details.text.create

import be.hogent.faith.faith.TokenResult
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.retrofitmodels.OverwritableTextDetail

interface ITextDetailRepository {
    suspend fun loadDetail(textDetail:TextDetail):TokenResult
    suspend fun createNewDetail(text:String):TextDetailResult
    suspend fun overwriteDetail(overwritableTextDetail: OverwritableTextDetail):TextDetailResult
}
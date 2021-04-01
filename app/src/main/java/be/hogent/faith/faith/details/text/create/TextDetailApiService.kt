package be.hogent.faith.faith.details.text.create

import be.hogent.faith.faith.Token
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.retrofitmodels.OverwritableTextDetail
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface TextDetailApiService {
    @PUT("/text/text")
    fun loadExistingDetail(@Body textDetail:TextDetail): Call<Token>
    @PUT("/text")
    fun createNewDetail(@Body text:Token): Call<TextDetail>
    @PUT("/text/over")
    fun overwriteDetail(@Body overwritableTextDetail: OverwritableTextDetail): Call<TextDetail>
}
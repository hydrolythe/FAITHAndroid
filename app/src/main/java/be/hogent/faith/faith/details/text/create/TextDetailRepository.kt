package be.hogent.faith.faith.details.text.create

import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.RetrofitRequestAdapter
import be.hogent.faith.faith.Token
import be.hogent.faith.faith.TokenResult
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.retrofitmodels.OverwritableTextDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TextDetailRepository:ITextDetailRepository {
    override suspend fun loadDetail(textDetail: TextDetail): TokenResult {
        return withContext(Dispatchers.IO){
            val request = RetrofitAdapter.textDetailApiService.loadExistingDetail(textDetail)
            try {
                val result = request.execute()
                TokenResult(success=result.body())
            } catch (e:Exception){
                TokenResult(exception=e)
            }
        }
    }

    override suspend fun createNewDetail(text: String): TextDetailResult {
        return withContext(Dispatchers.IO){
            val request = RetrofitAdapter.textDetailApiService.createNewDetail(Token(text))
            try {
                val result = request.execute()
                TextDetailResult(success=result.body())
            } catch (e:Exception){
                TextDetailResult(exception=e)
            }
        }
    }

    override suspend fun overwriteDetail(overwritableTextDetail: OverwritableTextDetail): TextDetailResult {
        return withContext(Dispatchers.IO){
            val request = RetrofitAdapter.textDetailApiService.overwriteDetail(overwritableTextDetail)
            try {
                val result = request.execute()
                TextDetailResult(success=result.body())
            } catch (e:Exception){
                TextDetailResult(exception=e)
            }
        }
    }
}
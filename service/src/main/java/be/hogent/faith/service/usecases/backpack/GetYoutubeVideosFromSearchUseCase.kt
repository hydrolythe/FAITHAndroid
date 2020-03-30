package be.hogent.faith.service.usecases.backpack

import ApiResult
import android.util.Log
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.service.network.YoutubeApi
import be.hogent.faith.service.network.YoutubeConfig
import be.hogent.faith.service.network.asDomainModel
import java.io.IOException

class GetYoutubeVideosFromSearchUseCase() {

    private val VIDEOPART = "snippet"
    private val SAFESEARCH = "strict"
    private val TYPE = "video"
    private val MAXRESULTS = "10"
    private val FIELDS = "items(id(videoId),snippet(title,description))"

    /**
     * If our request was successful -> returns a list of YoutubeVideoSnippets wrapped in a result class
     * else -> exception
     * */
    suspend fun getYoutubeVideos(searchString : String): ApiResult<List<YoutubeVideoDetail>> = try {
        val data= YoutubeApi.apiService.getYouTubeVideosAsync(
            YoutubeConfig().getKey(), VIDEOPART, searchString, SAFESEARCH, TYPE, MAXRESULTS, FIELDS)
            .await()
        ApiResult.Success(asDomainModel(data.items))
    }catch (e: Exception) {
        ApiResult.Error(IOException("Error receiving videos", e))
    }
}
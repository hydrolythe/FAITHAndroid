package be.hogent.faith.service.usecases.backpack

import ApiResult
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.service.network.YoutubeApi
import be.hogent.faith.service.network.YoutubeConfig
import be.hogent.faith.service.network.asDomainModel
import be.hogent.faith.service.usecases.base.FlowableUseCase
import io.reactivex.Flowable
import io.reactivex.Scheduler
import java.io.IOException

class GetYoutubeVideosFromSearchUseCase(
    observeScheduler: Scheduler
) : FlowableUseCase<List<YoutubeVideoDetail>, GetYoutubeVideosFromSearchUseCase.Params>(observeScheduler){

    private val VIDEOPART = "snippet"
    private val SAFESEARCH = "strict"
    private val TYPE = "video"
    private val MAXRESULTS = "10"
    private val FIELDS = "items(id(videoId),snippet(title,description))"

    /**
     * If our request was successful -> returns a list of YoutubeVideoSnippets wrapped in a result class
     * else -> exception
     * */
    override fun buildUseCaseObservable(params: Params): Flowable<List<YoutubeVideoDetail>> {
        return YoutubeApi.apiService.getYouTubeVideosAsync(
           YoutubeConfig().getKey(), VIDEOPART, params.searchString, SAFESEARCH, TYPE, MAXRESULTS, FIELDS)
            .map {
                asDomainModel(it.items)
            }
            .toFlowable()
    }

    data class Params(val searchString: String)
}
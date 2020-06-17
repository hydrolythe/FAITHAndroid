package be.hogent.faith.service.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface IYoutubeService {

    /**
     * gets the Youtube snippets
     * Key: API key
     * Part: which type of return list (in our case snippet)
     * q: the query string
     * safeSearch: strict filtering
     * type: video
     * maxResults: amount of results that the API returns
     * fields: YouTube API best practice: only receive the fields you need
     * */
    @GET("search")
    fun getYouTubeVideosAsync(
        @Query("key") apiKey: String?,
        @Query("part") videoPart: String?,
        @Query("q") searchString: String?,
        @Query("safeSearch") safeSearch: String?,
        @Query("type") type: String?,
        @Query("maxResults") maxResults: String?,
        @Query("fields") fields: String?
    ): Single<NetworkVideo>
}

/**
 * creates the api service
 * */
object YoutubeApi {

    /**
     * creates the api service when needed
     * */
    val apiService: IYoutubeService by lazy {
        provideRetrofit()
            .create(IYoutubeService::class.java)
    }
}

val gson: Gson =
    GsonBuilder().setLenient().create()

/**
 * provides the retrofit connection when needed
 * */
fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(YoutubeConfig().getBaseURL())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
}

class YoutubeConfig() {

    private val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    /**
     * TODO temporary, SHOULD BE STORED IN A KEY VAULT!!!
     * */

    private val API_KEY = "AIzaSyAnxKPALTBvTnJAeSSYiXkbD9RqLigWbQY"

    fun getKey(): String {
        return API_KEY
    }

    fun getBaseURL(): String {
        return BASE_URL
    }
}
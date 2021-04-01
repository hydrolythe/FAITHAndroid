package be.hogent.faith.faith

import android.content.Context
import be.hogent.faith.faith.backpack.BackpackApiService
import be.hogent.faith.faith.details.audio.AudioDetailApiService
import be.hogent.faith.faith.details.drawing.create.DrawingDetailApiService
import be.hogent.faith.faith.details.photo.create.CreatePhotoApiService
import be.hogent.faith.faith.details.text.create.TextDetailApiService
import be.hogent.faith.faith.loginOrRegister.registerAvatar.RegisterAvatarApiService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy

object RetrofitRequestAdapter {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(UUIDJsonAdapter())
        .add(FileJsonAdapter()).add(DateJsonAdapter()).build()
    var retrofit: Retrofit? = null
    fun getinstance(context: Context): Retrofit? {
        val client: OkHttpClient
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(AddCookiesInterceptor(context))
        builder.addInterceptor(ReceivedCookiesInterceptor(context))
        builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.1.203",5555)))
        client = builder.build()
        if (retrofit == null) {
            retrofit = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(
                    CoroutineCallAdapterFactory()
                ).baseUrl(BASE_URL)
                .client(client).build()
        }
        return retrofit
    }

    val userApiService: UserApiService by lazy {
        retrofit?.create(UserApiService::class.java)!!
    }
    val registerAvatarApiService: RegisterAvatarApiService by lazy {
        retrofit?.create(RegisterAvatarApiService::class.java)!!
    }
    val backpackApiService: BackpackApiService by lazy {
        retrofit?.create(BackpackApiService::class.java)!!
    }
    val audioDetailApiService: AudioDetailApiService by lazy{
        retrofit?.create(AudioDetailApiService::class.java)!!
    }
    val drawingDetailApiService: DrawingDetailApiService by lazy{
        retrofit?.create(DrawingDetailApiService::class.java)!!
    }
    val createPhotoApiService: CreatePhotoApiService by lazy{
        retrofit?.create(CreatePhotoApiService::class.java)!!
    }
    val textDetailApiService: TextDetailApiService by lazy{
        retrofit?.create(TextDetailApiService::class.java)!!
    }
}
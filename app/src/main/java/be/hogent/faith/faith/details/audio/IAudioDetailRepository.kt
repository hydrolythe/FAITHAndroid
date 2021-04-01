package be.hogent.faith.faith.details.audio

import be.hogent.faith.faith.models.detail.AudioDetail
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

interface IAudioDetailRepository {
    suspend fun getAudioDetail(file:File): AudioDetailResult
}
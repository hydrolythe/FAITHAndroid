package be.hogent.faith.faith.backpack

import be.hogent.faith.faith.DetailResult
import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.VoidResult
import be.hogent.faith.faith.detailscontainer.DetailsContainerResult
import be.hogent.faith.faith.detailscontainer.IDetailsContainerRepository
import be.hogent.faith.faith.models.Backpack
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.detail.AudioDetail
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.DetailType
import be.hogent.faith.faith.models.detail.DrawingDetail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import be.hogent.faith.faith.models.detail.PhotoDetail
import be.hogent.faith.faith.models.detail.TextDetail
import be.hogent.faith.faith.models.detail.VideoDetail
import be.hogent.faith.faith.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.models.retrofitmodels.DetailFile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BackpackRepository(val firebaseAuth: FirebaseAuth) : IDetailsContainerRepository {
    override suspend fun loadDetails(): DetailsContainerResult {
        return withContext(Dispatchers.IO) {
            val request =
                RetrofitAdapter.backpackApiService.getDetails(firebaseAuth.currentUser?.uid)
            try {
                val result = request.execute()
                val detailArray = result.body()
                val backpack = Backpack()
                val details = mutableListOf<Detail>()
                if (detailArray?.details != null) {
                    detailArray.details.forEach {
                        when (it.detailType) {
                            DetailType.TEXT -> details.add(
                                TextDetail(
                                    it.file,
                                    it.title,
                                    it.uuid,
                                    it.dateTime
                                )
                            )
                            DetailType.AUDIO -> details.add(
                                AudioDetail(
                                    it.file,
                                    it.title,
                                    it.uuid,
                                    it.dateTime
                                )
                            )
                            DetailType.DRAWING -> details.add(
                                DrawingDetail(
                                    it.file,
                                    it.thumbnail,
                                    it.title,
                                    it.uuid
                                )
                            )
                            DetailType.PHOTO -> details.add(
                                PhotoDetail(
                                    it.file,
                                    it.thumbnail,
                                    it.title,
                                    it.uuid
                                )
                            )
                            DetailType.VIDEO -> details.add(
                                VideoDetail(
                                    it.file,
                                    it.title,
                                    it.uuid,
                                    it.dateTime
                                )
                            )
                            DetailType.YOUTUBE -> details.add(
                                YoutubeVideoDetail(
                                    it.file,
                                    it.title,
                                    it.uuid,
                                    it.title,
                                    it.dateTime
                                )
                            )
                        }
                    }
                    backpack.setDetails(details)
                }
                DetailsContainerResult(success = backpack)
            } catch (e: Exception) {
                DetailsContainerResult(exception = e)
            }
        }
    }

    override suspend fun getCurrentDetailFile(detail: Detail): DetailResult {
        return withContext(Dispatchers.IO) {
            val request = RetrofitAdapter.backpackApiService.getCurrentDetailFile(
                firebaseAuth.currentUser?.uid,
                detail)
            try {
                val result = request.execute()
                DetailResult(success = result.body())
            } catch (e: Exception) {
                DetailResult(exception = e)
            }
        }
    }

    override suspend fun saveDetail(user: User, expandedDetail: ExpandedDetail): VoidResult {
        return withContext(Dispatchers.IO) {
            val request = RetrofitAdapter.backpackApiService.postDetails(user.uuid, expandedDetail)
            try {
                val result = request.execute()
                VoidResult(success = result.body())
            } catch (e: Exception) {
                VoidResult(exception = e)
            }
        }
    }

    override suspend fun deleteDetail(detail: Detail): VoidResult {
        return withContext(Dispatchers.IO) {
            val request = RetrofitAdapter.backpackApiService.deleteDetails(
                firebaseAuth.currentUser?.uid,
                detail
            )
            try {
                val result = request.execute()
                VoidResult(success = result.body())
            } catch (e: Exception) {
                VoidResult(exception = e)
            }
        }
    }

    override suspend fun downloadFile(file: File): VoidResult {
        return withContext(Dispatchers.IO) {
            val request = RetrofitAdapter.backpackApiService.getFileOutput(DetailFile(file))
            try {
                val result = request.execute()
                VoidResult(success = result.body())
            } catch (e: Exception) {
                VoidResult(exception = e)
            }
        }
    }
}
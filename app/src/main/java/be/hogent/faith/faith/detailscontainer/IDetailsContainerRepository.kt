package be.hogent.faith.faith.detailscontainer

import be.hogent.faith.faith.DetailResult
import be.hogent.faith.faith.VoidResult
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import okhttp3.ResponseBody
import java.io.File

interface IDetailsContainerRepository {
    suspend fun loadDetails():DetailsContainerResult
    suspend fun getCurrentDetailFile(detail:Detail):DetailResult
    suspend fun saveDetail(user: User, expandedDetail:ExpandedDetail): VoidResult
    suspend fun deleteDetail(detail: Detail): VoidResult
    suspend fun downloadFile(file: File): VoidResult
}
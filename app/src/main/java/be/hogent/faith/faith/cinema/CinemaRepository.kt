package be.hogent.faith.faith.cinema

import be.hogent.faith.faith.DetailResult
import be.hogent.faith.faith.VoidResult
import be.hogent.faith.faith.detailscontainer.DetailsContainerResult
import be.hogent.faith.faith.detailscontainer.IDetailsContainerRepository
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.models.detail.ExpandedDetail
import java.io.File

class CinemaRepository:IDetailsContainerRepository {
    override suspend fun loadDetails(): DetailsContainerResult {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentDetailFile(detail: Detail): DetailResult {
        TODO("Not yet implemented")
    }

    override suspend fun saveDetail(user: User, expandedDetail: ExpandedDetail): VoidResult {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDetail(detail: Detail): VoidResult {
        TODO("Not yet implemented")
    }

    override suspend fun downloadFile(file: File): VoidResult {
        TODO("Not yet implemented")
    }
}
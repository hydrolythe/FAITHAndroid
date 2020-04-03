package be.hogent.faith.database.detailcontainer

import be.hogent.faith.database.common.DetailMapper
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.usecases.repository.IDetailContainerRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

open class DetailContainerRepository<T : DetailsContainer>(
    private val userMapper: UserMapper,
    private val detailMapper: DetailMapper,
    private val database: DetailContainerDatabase<T>
) : IDetailContainerRepository<T> {

    override fun insertDetail(detail: Detail, user: User): Maybe<Detail> {
        return database.insert(
            detailMapper.mapToEntity(detail),
            userMapper.mapToEntity(user)
            ).map {
            detailMapper.mapFromEntity(it)
        }
    }

    override fun get(): Flowable<List<Detail>> {
        return database.getAll().map { detailMapper.mapFromEntities(it)
    } }

    override fun deleteDetail(detail: Detail): Completable {
        return database.delete(detailMapper.mapToEntity(detail))
    }
}
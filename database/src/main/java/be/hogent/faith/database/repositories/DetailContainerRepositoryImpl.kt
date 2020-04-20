package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseDetailContainerRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.DetailContainerRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

abstract class DetailContainerRepositoryImpl<T : DetailsContainer>(
    private val userMapper: UserMapper,
    private val detailMapper: DetailMapper,
    protected val firebaseDetailContainerRepository: FirebaseDetailContainerRepository<T>
) : DetailContainerRepository<T> {

    override fun insertDetail(detail: Detail, user: User): Maybe<Detail> {
        return firebaseDetailContainerRepository.insert(
            detailMapper.mapToEntity(detail),
            userMapper.mapToEntity(user)
            ).map {
            detailMapper.mapFromEntity(it)
        }
    }

    override fun get(): Flowable<List<Detail>> {
        return firebaseDetailContainerRepository.get().map { detailMapper.mapFromEntities(it)
    } }

    override fun deleteDetail(detail: Detail): Completable {
        return firebaseDetailContainerRepository.delete(detailMapper.mapToEntity(detail))
    }
}
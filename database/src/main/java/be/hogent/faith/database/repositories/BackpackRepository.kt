package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseBackpackRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.repository.IBackpackRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable

open class BackpackRepository(
    private val userMapper: UserMapper,
    private val detailMapper: DetailMapper,
    private val firebaseBackpackRepository: FirebaseBackpackRepository
) : IBackpackRepository {

    override fun insertDetail(detail: Detail, user: User): Maybe<Detail> {
        return firebaseBackpackRepository.insert(
            detailMapper.mapToEntity(detail),
            userMapper.mapToEntity(user)
        ).map {
            detailMapper.mapFromEntity(it)
        }
    }

    override fun get(): Observable<List<Detail>> {
        return firebaseBackpackRepository.get()
            .map { detailMapper.mapList(it) }
    }

    override fun deleteDetail(detail: Detail): Completable {
        return firebaseBackpackRepository.delete(detailMapper.mapToEntity(detail))
    }
}
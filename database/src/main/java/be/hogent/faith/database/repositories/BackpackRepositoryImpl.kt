package be.hogent.faith.database.repositories

import be.hogent.faith.database.firebase.FirebaseBackpackRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.Mapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.repository.BackpackRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

open class BackpackRepositoryImpl (
    private val userMapper : UserMapper,
    private val detailMapper: DetailMapper,
    private val firebaseBackpackRepository: FirebaseBackpackRepository
): BackpackRepository {
    override fun insertTextDetail(detail: TextDetail, user: User): Maybe<Detail> {
        return firebaseBackpackRepository.insert(
            detailMapper.mapToEntity(detail),
            userMapper.mapToEntity(user)
       ).map {
            detailMapper.mapFromEntity(it)
        }
    }

    override fun get(): Flowable<List<Detail>> {
        return firebaseBackpackRepository.get().map { detailMapper.mapFromEntities(it)
    }}
}
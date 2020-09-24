package be.hogent.faith.database.detailcontainer

import be.hogent.faith.database.common.DetailMapper
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.domain.models.DetailsContainer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.service.encryption.EncryptedDetail
import be.hogent.faith.service.encryption.EncryptedDetailsContainer
import be.hogent.faith.service.repositories.IDetailContainerRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

open class DetailContainerRepository<T : DetailsContainer>(
    private val userMapper: UserMapper,
    private val detailMapper: DetailMapper,
    private val containerMapper: DetailContainerMapper,
    private val database: DetailContainerDatabase<T>
) : IDetailContainerRepository<T> {

    override fun insertDetail(
        encryptedDetail: EncryptedDetail,
        user: User
    ): Completable {
        return database.insertDetail(
            detailMapper.mapToEntity(encryptedDetail),
            userMapper.mapToEntity(user)
        )
    }

    override fun getEncryptedContainer(): Single<EncryptedDetailsContainer> {
        return database.getContainer()
            .map(containerMapper::mapFromEntity)
            .toSingle()
    }

    override fun getAll(): Flowable<List<EncryptedDetail>> {
        return database.getAll().map {
            detailMapper.mapFromEntities(it)
        }
    }

    override fun deleteDetail(detail: Detail): Completable {
        return database.delete(detail)
    }

    override fun saveEncryptedContainer(encryptedDetailsContainer: EncryptedDetailsContainer): Completable {
        return database.insertContainer(
            containerMapper.mapToEntity(encryptedDetailsContainer)
        )
    }
}
package be.hogent.faith.database.repositories

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.firebase.FirebaseCinemaRepository
import be.hogent.faith.database.mappers.DetailMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.util.factory.DetailFactory
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import org.junit.Test

class CinemaRepositoryImplTest {
    private val firebaseCinemaRepository = mockk<FirebaseCinemaRepository>(relaxed = true)
    private val userMapper = mockk<UserMapper>()
    private val detailMapper = mockk<DetailMapper>()

    private val cinemaRepository =
        DetailContainerRepositoryImpl<Cinema>(userMapper, detailMapper, firebaseCinemaRepository)

    private val user = UserFactory.makeUser(0)
    private val userEntity = EntityFactory.makeUserEntity()
    private val detailEntity = EntityFactory.makeDetailEntity()
    private val detail = DetailFactory.makeRandomDetail()
    private val detailEntitiesList = EntityFactory.makeDetailEntityList(2)
    private val detailList =
        listOf(DetailFactory.makeRandomDetail(), DetailFactory.makeRandomDetail())

    @Test
    fun cinemaRepository_get_existingDetails_succeeds() {
        every { firebaseCinemaRepository.get() } returns Flowable.just(
            detailEntitiesList
        )
        stubMapperFromEntityList(detailList, detailEntitiesList)
        cinemaRepository.get()
            .test()
            .assertValue(detailList)
    }

    @Test
    fun cinemaRepository_insertDetail_succeeds() {
        every { firebaseCinemaRepository.insert(detailEntity, userEntity) } returns Maybe.just(
            detailEntity
        )
        stubMapperToEntity(detail, detailEntity)
        stubMapperFromEntity(detail, detailEntity)
        stubMapperToEntityUser(user, userEntity)
        cinemaRepository.insertDetail(detail, user)
            .test()
            .assertValue { it.uuid == detail.uuid }
    }

    @Test
    fun cinemaRepository_deleteDetail_succeeds() {
        every { firebaseCinemaRepository.delete(detailEntity) } returns Completable.complete()
        stubMapperToEntity(detail, detailEntity)
        cinemaRepository.deleteDetail(detail)
            .test().assertComplete().assertNoErrors()
    }

    private fun stubMapperFromEntity(model: Detail, entity: DetailEntity) {
        every { detailMapper.mapFromEntity(entity) } returns model
    }

    private fun stubMapperFromEntityList(model: List<Detail>, entity: List<DetailEntity>) {
        every { detailMapper.mapFromEntities(entity) } returns model
    }

    private fun stubMapperToEntity(model: Detail, entity: DetailEntity) {
        every { detailMapper.mapToEntity(model) } returns entity
    }

    private fun stubMapperToEntityUser(model: User, entity: UserEntity) {
        every { userMapper.mapToEntity(model) } returns entity
    }
}
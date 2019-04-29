package be.hogent.faith.database.repositories

import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.daos.UserDao
import be.hogent.faith.database.database.EntityDatabase
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.mappers.EventWithDetailsMapper
import be.hogent.faith.database.mappers.UserMapper
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.util.factory.UserFactory
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test

class UserEventRepositoryImplTest {
    private val detailDao = mockk<DetailDao>()
    private val eventDao = mockk<EventDao>()
    private val userDao = mockk<UserDao>()

    private val database = mockk<EntityDatabase>(relaxed = true)

    private val eventWithDetailsMapper = mockk<EventWithDetailsMapper>()
    private val userMapper = mockk<UserMapper>()

    private val userRepository = UserRepositoryImpl(database, userMapper, eventWithDetailsMapper)

    private val userWithoutEvents = UserFactory.makeUser(0)
    private val userEntity = EntityFactory.makeUserEntity()
    private val userWithoutEvents2 = UserFactory.makeUser(0)
    private val userEntity2 = EntityFactory.makeUserEntity()

    @Before
    fun setUp() {
        every { database.userDao() } returns userDao
        every { database.eventDao() } returns eventDao
        every { database.detailDao() } returns detailDao
    }

    @Test
    fun userRepository_deleteUserCompletes() {
        every { userDao.delete(userEntity) } returns Completable.complete()
        stubUserMapperToEntity(userWithoutEvents, userEntity)
        userRepository.delete(userWithoutEvents).test().assertComplete()
    }

    @Test
    fun userRepository_get_nonExistingUser_errors() {
        every { userDao.getUser(userWithoutEvents.uuid) } returns Flowable.empty()
        every { eventDao.getAllEventsWithDetails(userEntity.uuid) } returns Flowable.empty()
        userRepository.get(userWithoutEvents.uuid)
            .test()
            .assertNoValues()
    }

    @Test
    fun userRepository_getAll_returnsFlowable() {
        every { userDao.getAllUsers() } returns Flowable.just(
            listOf(userEntity, userEntity2)
        )
        stubUsersMapperFromEntity(listOf(userWithoutEvents, userWithoutEvents2), listOf(userEntity, userEntity2))
        userRepository.getAll()
            .test()
            .assertValue {
                it.size == 2
            }
    }

    // TODO make test succeed
    /*  @Test
      fun userRepository_get_existingUser_succeeds() {
          every { userDao.getUser(userWithoutEvents.uuid) } returns Flowable.just(
              userEntity
          )
          every { eventDao.getAllEventsWithDetails(userEntity.uuid) } returns Flowable.just(
              eventWithDetailsList
          )

          stubUserMapperFromEntity(userWithoutEvents, userEntity)
          stubEventWithDetailsMapperFromEntity(eventList, eventWithDetailsList)

          userRepository.get(userWithoutEvents.uuid)
              .test()
              .assertValue { it.uuid == userWithoutEvents.uuid && it.events.size == eventList.size }
      }*/

    private fun stubUserMapperFromEntity(model: User, entity: UserEntity) {
        every { userMapper.mapFromEntity(entity) } returns model
    }

    private fun stubUsersMapperFromEntity(models: List<User>, entities: List<UserEntity>) {
        every { userMapper.mapFromEntities(entities) } returns models
    }

    private fun stubUserMapperToEntity(model: User, entity: UserEntity) {
        every { userMapper.mapToEntity(model) } returns entity
    }

    private fun stubEventWithDetailsListMapperFromEntity(models: List<Event>, entities: List<EventWithDetails>) {
        every { eventWithDetailsMapper.mapFromEntities(entities) } returns models
    }
}
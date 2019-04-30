package be.hogent.faith.database.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.daos.UserDao
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import be.hogent.faith.util.factory.DataFactory
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class EntityDatabaseTest {

    private lateinit var eventDao: EventDao
    private lateinit var detailDao: DetailDao
    private lateinit var userDao: UserDao

    private lateinit var db: EntityDatabase

    private val userUuid = DataFactory.randomUUID()
    private val eventUuid = DataFactory.randomUUID()
    private val eventDate = DataFactory.randomDateTime()
    private val eventFile = DataFactory.randomFile()
    private val userEntity = UserEntity(userUuid, "name")
    //    private val userEntity = UserEntity(userUuid, "name", "avatar")
    private val eventEntity = EventEntity(eventDate, "testDescription", eventFile, null, eventUuid, userUuid)
    private val eventNotes = DataFactory.randomString()

    // Required to make sure Room executes all operations instantly
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EntityDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = db.eventDao()
        detailDao = db.detailDao()
        userDao = db.userDao()
    }

    @Test
    fun entityDatabase_singleEvent_noDetails_isAdded() {
        // Arrange
        val testSubscriber = TestSubscriber<EventWithDetails>()

        // Act
        userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(eventDao.getEventWithDetails(eventUuid))
            .subscribe(testSubscriber)

        // Assert
        testSubscriber.assertValue {
            it.eventEntity == eventEntity
        }.dispose()
    }

    @Test
    fun entityDatabase_singleEvent_withDetails_isAdded() {
        // Arrange
        val detail1 = PictureDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail1")
        )
        val detail2 = TextDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail2")
        )

        val arrange = userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(detailDao.insert(detail1))
            .andThen(detailDao.insert(detail2))

        // Act
        val act = arrange.andThen(eventDao.getEventWithDetails(eventUuid))

        // Assert
        act
            .test()
            .assertValue {
                it.eventEntity == eventEntity && it.detailEntities.size == 2
                it.detailEntities.any { detailEntity -> detailEntity is TextDetailEntity }
                it.detailEntities.any { detailEntity -> detailEntity is PictureDetailEntity }
            }.dispose()
    }

    @Test
    fun entityDatabase_deleteEvent_detailsAreAlsoDeleted() {
        // Arrange
        val detail1 = PictureDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail1")
        )
        val detail2 = TextDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail2")
        )

        val arrange = userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(detailDao.insert(detail1))
            .andThen(detailDao.insert(detail2))

        // Act
        // Chaining two "andThen" calls isn't possible,
        // so by saving the completable we can just subscribe twice
        val act = arrange.andThen(eventDao.delete(eventEntity))

        // Assert
        act.andThen(eventDao.getEventWithDetails(eventUuid))
            .test()
            .assertEmpty()
        act.andThen(detailDao.getDetailsForEvent(eventUuid))
            .test()
            .assertValue { it.isEmpty() }
            .dispose()
    }

    @Test
    fun entityDatabase_deleteUser_EventAndDetailsAreAlsoDeleted() {
        // Arrange
        val detail1 = PictureDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail1")
        )
        val detail2 = TextDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail2")
        )
        val arrange = userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(detailDao.insert(detail1))
            .andThen(detailDao.insert(detail2))

        // Act
        // Chaining two "andThen" calls isn't possible,
        // so by saving the completable we can just subscribe twice
        val act = arrange.andThen(userDao.delete(userEntity))

        // Assert
        act.andThen(userDao.getUser(userUuid))
            .test()
            .assertEmpty()
        act.andThen(eventDao.getAllEventsWithDetails(userUuid))
            .test()
            .assertValue { it.isEmpty() }
        act.andThen(detailDao.getDetailsForEvent(eventUuid))
            .test()
            .assertValue { it.isEmpty() }
            .dispose()
    }

    @Test
    fun entityDatabase_singleUser_withNoEvents_isAdded() {
        // Arrange
//        val user = UserEntity(userUuid, "name", "avatar")
        // TODO: add avatar again
        val user = UserEntity(userUuid, "name")
        val arrange = userDao.insert(user)

        // Act
        val act = arrange.andThen(userDao.getUser(userUuid))

        // Assert
        act
            .test()
            .assertValue {
                it.uuid == userUuid
            }.dispose()
    }

    @Test
    fun entityDatabase_getAllEventsWithDetailsForAUser_chronological() {
        // Arrange
        val eventEntityLater =
            EventEntity(
                eventDate.minusDays(10),
                "testDescription",
                eventFile,
                DataFactory.randomString(),
                DataFactory.randomUUID(),
                userUuid
            )
        val eventEntity =
            EventEntity(eventDate, "testDescription", eventFile, DataFactory.randomString(), eventUuid, userUuid)

        val detail1 = PictureDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail1")
        )
        val detail2 = TextDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail2")
        )

        val arrange = userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(detailDao.insert(detail1))
            .andThen(detailDao.insert(detail2))
            .andThen(eventDao.insert(eventEntityLater))

        // Act
        val act = arrange.andThen(eventDao.getAllEventsWithDetails(userUuid))

        // Assert
        act
            .test()
            .assertValue {
                it.size == 2
                it[0].eventEntity.uuid == eventEntity.uuid
                it[1].eventEntity.uuid == eventEntityLater.uuid
            }.dispose()
    }

    @Test
    fun getDetails_onlyOneType_receives() {
        // Arrange
        val detail1 = PictureDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail1")
        )
        val arrange = userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(detailDao.insert(detail1))

        // Act
        val act = arrange.andThen(detailDao.getDetailsForEvent(eventUuid))

        // Assert
        act
            .test()
            .assertValue { result -> result.size == 1 }
    }

    @Test
    fun getDetails_multipleTypes_receives() {
        // Arrange
        val detail1 = PictureDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail1")
        )
        val detail2 = TextDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail2")
        )
        val detail3 = AudioDetailEntity(
            eventUuid = eventUuid,
            file = File("path/detail3")
        )
        val arrange = userDao.insert(userEntity)
            .andThen(eventDao.insert(eventEntity))
            .andThen(detailDao.insert(detail1))
            .andThen(detailDao.insert(detail2))
            .andThen(detailDao.insert(detail3))

        // Act
        val act = arrange.andThen(detailDao.getDetailsForEvent(eventUuid))

        // Assert
        act
            .test()
            .assertValue { result ->
                result.size == 3 &&
                        result.any { it is AudioDetailEntity } &&
                        result.any { it is PictureDetailEntity } &&
                        result.any { it is TextDetailEntity }
            }
    }

    @After
    fun breakDown() {
        db.close()
    }
}

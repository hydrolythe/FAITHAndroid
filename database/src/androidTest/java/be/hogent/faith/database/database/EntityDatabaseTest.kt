package be.hogent.faith.database.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.daos.UserDao
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity
import be.hogent.faith.database.models.relations.EventWithDetails
import io.reactivex.subscribers.TestSubscriber
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.io.File
import java.util.UUID

class EntityDatabaseTest {

    private lateinit var eventDao: EventDao
    private lateinit var detailDao: DetailDao
    private lateinit var userDao: UserDao

    private lateinit var db: EntityDatabase

    private val userUuid = UUID.randomUUID()
    private val eventUuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")
    private val eventDate = LocalDateTime.of(2018, 10, 28, 7, 33)!!
    private val eventFile = File("path/to/eventFile")
    private val userEntity = UserEntity(userUuid)
    private val eventEntity = EventEntity(eventDate, "testDescription", eventFile, eventUuid, userUuid)

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
        val eventEntity = EventEntity(eventDate, "testDescription", eventFile, eventUuid, userUuid)
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
        val eventEntity = EventEntity(eventDate, "testDescription", eventFile, eventUuid, userUuid)

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
        val user = UserEntity(uuid = userUuid)
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
            EventEntity(eventDate.minusDays(10), "testDescription", eventFile, UUID.randomUUID(), userUuid)
        val eventEntity = EventEntity(eventDate, "testDescription", eventFile, eventUuid, userUuid)

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

    @After
    fun breakDown() {
        db.close()
    }
}

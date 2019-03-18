package be.hogent.faith.database.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.database.models.EventEntity
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

    private lateinit var db: EntityDatabase

    private val eventUuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")
    private val eventDate = LocalDateTime.of(2018, 10, 28, 7, 33)!!
    private val eventFile = File("path/to/eventFile")
    private val eventEntity = EventEntity(eventDate, "testDescription", eventFile, eventUuid)

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
    }

    @Test
    fun entityDatabase_singleEntry_noDetails_isAdded() {
        val testSubscriber = TestSubscriber<EventWithDetails>()
        eventDao.insert(eventEntity)
            .andThen(eventDao.getEventWithDetails(eventUuid))
            .subscribe(testSubscriber)

        testSubscriber.assertValue {
            it.eventEntity == eventEntity
        }
            .dispose()
    }

    @Test
    fun entityDatabase_singleEntry_withDetails_isAdded() {
        // Arrange
        val detail1 = DetailEntity(eventUuid = eventUuid, type = DetailTypeEntity.VIDEO, file = File("path/detail1"))
        val detail2 = DetailEntity(eventUuid = eventUuid, type = DetailTypeEntity.AUDIO, file = File("path/detail2"))

        val arrange = eventDao.insert(eventEntity)
            .andThen(detailDao.insert(detail1))
            .andThen(detailDao.insert(detail2))

        val act = arrange.andThen(eventDao.getEventWithDetails(eventUuid))

        // Assert
        act
            .test()
            .assertValue {
                it.eventEntity == eventEntity && it.detailEntities.size == 2
            }
    }

    @Test
    fun entityDatabase_deleteEntry_detailsAreAlsoDeleted() {
        // Arrange
        val eventEntity = EventEntity(eventDate, "testDescription", eventFile, eventUuid)

        val detail1 = DetailEntity(eventUuid = eventUuid, type = DetailTypeEntity.VIDEO, file = eventFile)
        val detail2 = DetailEntity(eventUuid = eventUuid, type = DetailTypeEntity.AUDIO, file = eventFile)

        val arrange = eventDao.insert(eventEntity)
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
    }

    @After
    fun breakDown() {
        db.close()
    }
}

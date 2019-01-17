package be.hogent.faith.database.database

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.database.models.EventEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.util.UUID

class EntityDatabaseTest {

    private lateinit var eventDao: EventDao
    private lateinit var detailDao: DetailDao

    private lateinit var db: EntityDatabase

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
        val uuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")
        val date = LocalDateTime.of(2018, 10, 28, 7, 33)
        val eventEntity = EventEntity(uuid, date, "testDescrption")

        eventDao.insert(eventEntity)

        eventDao.getEventWithDetails(uuid)
            .test()
            .assertValue { it.eventEntity!!.equals(eventEntity) }


    }

    @Test
    fun entityDatabase_singleEntry_withDetails_isAdded() {
        val uuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")
        val date = LocalDateTime.of(2018, 10, 28, 7, 33)
        val eventEntity = EventEntity(uuid, date, "testDescrption")

        val detail1 = DetailEntity(eventUuid = uuid, type = DetailTypeEntity.VIDEO)
        val detail2 = DetailEntity(eventUuid = uuid, type = DetailTypeEntity.AUDIO)
        eventEntity.details.add(detail1)
        eventEntity.details.add(detail2)

        eventDao.insert(eventEntity)
        detailDao.insert(detail1)
        detailDao.insert(detail2)

        eventDao.getEventWithDetails(uuid)
            .test()
            .assertValue {
                it.eventEntity!!.equals(eventEntity) && it.detailEntities.size == 2
            }
    }

    @Test
    fun entityDatabase_deleteEntry_detailsAreAlsoDeleted() {
        val uuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")
        val date = LocalDateTime.of(2018, 10, 28, 7, 33)
        val eventEntity = EventEntity(uuid, date, "testDescrption")

        val detail1 = DetailEntity(eventUuid = uuid, type = DetailTypeEntity.VIDEO)
        val detail2 = DetailEntity(eventUuid = uuid, type = DetailTypeEntity.AUDIO)
        eventEntity.details.add(detail1)
        eventEntity.details.add(detail2)

        eventDao.insert(eventEntity)
        detailDao.insert(detail1)
        detailDao.insert(detail2)

        // Act
        eventDao.delete(eventEntity)

        // Assert
        eventDao.getEventWithDetails(uuid)
            .doOnEach{ Log.d("dbtest", "eventTest ${it.value}")}
            .test()
            .assertEmpty()
        detailDao.getDetailsForEvent(uuid)
            .test()
            .assertValue { it.isEmpty() }
    }

    @After
    fun breakDown() {
        db.close()
    }
}

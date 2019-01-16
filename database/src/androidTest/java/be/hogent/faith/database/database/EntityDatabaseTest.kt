package be.hogent.faith.database.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import be.hogent.faith.database.TestUtils.getValue
import be.hogent.faith.database.daos.DetailDao
import be.hogent.faith.database.daos.EventDao
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailTypeEntity
import be.hogent.faith.database.models.EventEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime
import java.util.*


class EntityDatabaseTest {

    private lateinit var eventDao: EventDao
    private lateinit var detailDao: DetailDao

    private lateinit var db: EntityDatabase

    //Required to force LiveData on main thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EntityDatabase::class.java).allowMainThreadQueries().build()
        eventDao = db.eventDao()
        detailDao = db.detailDao()
    }

    @Test
    fun EntityDatabase_singleEntry_noDetails_isAdded() {
        val uuid = UUID.fromString("d883853b-7b23-401f-816b-ed4231e6dd6a")
        val date = LocalDateTime.of(2018, 10, 28, 7, 33)
        val eventEntity = EventEntity(uuid, date, "testDescrption")

        eventDao.insert(eventEntity)
        val result = getValue(eventDao.getEventWithDetails(uuid)).eventEntity

        assertEquals(eventEntity, result)
    }

    @Test
    fun EntityDatabase_singleEntry_withDetails_isAdded() {
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

        val eventWithDetails = getValue(eventDao.getEventWithDetails(uuid))

        assertEquals(eventEntity, eventWithDetails.eventEntity)
        assertEquals(2, eventWithDetails.detailEntities.size)
    }

    @Test
    fun EntityDatabase_deleteEntry_detailsAreAlsoDeleted() {
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

        //Act
        eventDao.delete(eventEntity)

        //Assert
        assertNull(getValue(eventDao.getEventWithDetails(uuid)))
        assertEquals(0, getValue(detailDao.getDetailsForEvent(uuid)).size)
    }

    @After
    fun breakDown() {
        db.close()
    }

}

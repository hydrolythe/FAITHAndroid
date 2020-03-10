package be.hogent.faith.faith.library.eventfilters

import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.util.factory.DetailFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EventHasDetailTypeFilterTest {

    private val events = listOf(
        Event().apply {
            addDetail(DetailFactory.makeDrawingDetail())
            addDetail(DetailFactory.makeTextDetail())
        },
        Event().apply {
            addDetail(DetailFactory.makeAudioDetail())
            addDetail(DetailFactory.makeDrawingDetail())
        },
        Event().apply {
            addDetail(DetailFactory.makeAudioDetail())
            addDetail(DetailFactory.makeDrawingDetail())
            addDetail(DetailFactory.makeTextDetail())
        }
    )

    @Test
    fun `filter takes out all details that are not the given detail subclass`() {
        // Arrange
        val textFilter = EventHasDetailTypeFilter(TextDetail::class)

        // Act
        val filteredList = events.filter(textFilter)

        // Assert
        assertEquals(2, filteredList.size)
        assertTrue(filteredList.all { it.details.any { it is TextDetail } })
    }
}
package be.hogent.faith.faith.details.filter

import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.detailFilters.DetailTypeFilter
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DetailTypeFilterTest {

        private val details = listOf(
                mockk<AudioDetail>(), mockk<AudioDetail>(),
                mockk<TextDetail>(), mockk<TextDetail>(),
                mockk<PhotoDetail>(), mockk<PhotoDetail>(),
                mockk<DrawingDetail>(), mockk<DrawingDetail>()
        )

    @Test
    fun filterTextDetails() {
        // Arrange
        val textFilter = DetailTypeFilter(TextDetail::class)

        // Act
        val filteredList = details.filter(textFilter)

        // Assert
        assertEquals(2, filteredList.size)
        assertTrue(filteredList.all { details.any { it is TextDetail } })
    }

    @Test
    fun filterPhotoDetails() {
        // Arrange
        val photoFilter = DetailTypeFilter(PhotoDetail::class)

        // Act
        val filteredList = details.filter(photoFilter)

        // Assert
        assertEquals(2, filteredList.size)
        assertTrue(filteredList.all { details.any { it is PhotoDetail } })
    }

    @Test
    fun filterPhotoDetailsAndTextDetails() {
        // Arrange
        val photoFilter = DetailTypeFilter(PhotoDetail::class)
        val textFilter = DetailTypeFilter(TextDetail::class)
        // Act
        val filteredList = details.filter(photoFilter)
        val filteredList2 = details.filter(textFilter)

        val filteredList3 = mutableListOf<Detail>()
        filteredList3.addAll(filteredList)
        filteredList3.addAll(filteredList2)

        // Assert
        assertEquals(4, filteredList3.size)
    }
}
package be.hogent.faith.domain.details

import be.hogent.faith.domain.models.detail.PhotoDetail
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class PhotoDetailTest {
    private lateinit var photoDetail: PhotoDetail

    @Before
    fun setUp() {
        photoDetail = PhotoDetail(mockk<File>())
    }

    @Test
    fun `PhotoDetail getDuration returns the correct duration`() {
        Assert.assertEquals(5, photoDetail.getDuration())
    }
}
package be.hogent.faith.faith.cinema

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.TestUtils
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class CinemaCreateVideoViewModelTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var cinemaCreateVideoViewModel: CinemaCreateVideoViewModel

    private val detail_1 = PhotoDetail(
        title = "title 1",
        file = mockk<File>(),
        thumbnail = ""
    )
    private val detail_2 = TextDetail(
        title = "title 2",
        file = mockk<File>()
    )
    private val detail_3 = DrawingDetail(
        title = "title 3",
        file = mockk<File>(),
        thumbnail = ""
    )
    private val detail_4 = AudioDetail(
        title = "title 4",
        file = mockk<File>()
    )

    private val detailsObserver = Observer<List<Detail>> { }

    @Before
    fun setUp() {
        cinemaCreateVideoViewModel = CinemaCreateVideoViewModel(mockk())
        cinemaCreateVideoViewModel.selectedDetails.observeForever(detailsObserver)
    }

    @Test
    fun testAddingDetailsToList() {
        cinemaCreateVideoViewModel.addSelectedDetail(detail_1, 0)
        cinemaCreateVideoViewModel.addSelectedDetail(detail_2, 0)
        val details = TestUtils.getValue(cinemaCreateVideoViewModel.selectedDetails)
        Assert.assertEquals(2, details.size)
    }

    @Test
    fun testOrderDetails() {
        cinemaCreateVideoViewModel.addSelectedDetail(detail_1, 0)
        cinemaCreateVideoViewModel.addSelectedDetail(detail_2, 0)
        val details = TestUtils.getValue(cinemaCreateVideoViewModel.selectedDetails)
        Assert.assertEquals(detail_1, details.elementAt(0))
        Assert.assertEquals(detail_2, details.elementAt(1))
    }

    @Test
    fun testOrderDetailsWithRemoving() {
        cinemaCreateVideoViewModel.addSelectedDetail(detail_1, 0)
        cinemaCreateVideoViewModel.addSelectedDetail(detail_2, 0)
        cinemaCreateVideoViewModel.addSelectedDetail(detail_3, 0)

        var details = TestUtils.getValue(cinemaCreateVideoViewModel.selectedDetails)
        Assert.assertEquals(detail_2, details.elementAt(1))

        cinemaCreateVideoViewModel.removeSelectedDetail(detail_2, 0)

        details = TestUtils.getValue(cinemaCreateVideoViewModel.selectedDetails)

        Assert.assertEquals(detail_1, details.elementAt(0))
        Assert.assertEquals(detail_3, details.elementAt(1))
    }
}

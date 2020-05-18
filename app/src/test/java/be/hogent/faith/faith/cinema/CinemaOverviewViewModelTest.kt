package be.hogent.faith.faith.cinema

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.Cinema
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.TestUtils
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneOffset
import java.io.File

class CinemaOverviewViewModelTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CinemaOverviewViewModel

    private val detail_1_jan_photo = PhotoDetail(
        dateTime = LocalDateTime.of(2020, Month.JANUARY, 10, 10, 30),
        title = "title 1",
        file = mockk<File>(),
        thumbnail = ""
    )
    private val detail_2_jan_text = TextDetail(
        dateTime = LocalDateTime.of(2020, Month.JANUARY, 15, 10, 30),
        title = "title 2",
        file = mockk<File>()
    )
    private val detail_3_feb_drawing = DrawingDetail(
        dateTime = LocalDateTime.of(2020, Month.FEBRUARY, 15, 10, 30),
        title = "title 3",
        file = mockk<File>(),
        thumbnail = ""
    )
    private val detail_4_july_audio = AudioDetail(
        dateTime = LocalDateTime.of(2020, Month.MARCH, 15, 10, 30),
        title = "title 4",
        file = mockk<File>()
    )

    private val details =
        listOf(detail_4_july_audio, detail_1_jan_photo, detail_2_jan_text, detail_3_feb_drawing)

    // We need to immediatly observe on the [EventListViewModel.filteredEvents] or otherwise
    // the MediatorLiveData will not start processing updates from its sources.
    private val detailsObserver = Observer<List<Detail>> { }

    private val saveDetailsContainerDetailUseCase =
        mockk<SaveDetailsContainerDetailUseCase<Cinema>>()
    private val deleteDetailsContainerDetailUseCase =
        mockk<DeleteDetailsContainerDetailUseCase<Cinema>>()
    private val loadDetailFileUseCase = mockk<LoadDetailFileUseCase<Cinema>>()
    private val getCinemaDataUseCase = mockk<GetDetailsContainerDataUseCase<Cinema>>(relaxed = true)
    private val cinema: Cinema = mockk()

    @Before
    fun setUp() {
        every { cinema.getFiles() } returns details
        viewModel = CinemaOverviewViewModel(
            saveDetailsContainerDetailUseCase,
            deleteDetailsContainerDetailUseCase,
            loadDetailFileUseCase,
            cinema,
            getCinemaDataUseCase
        )
        viewModel.onFilesButtonClicked()
        viewModel.filteredDetails.observeForever(detailsObserver)
    }

    @After
    fun tearDown() {
        viewModel.filteredDetails.removeObserver(detailsObserver)
    }

    @Test
    fun `by default all details are shown, meaning no filters are active`() {
        // Assert
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)
        Assert.assertEquals(4, filteredDetails.size)
    }

    @Test
    fun `when clicking the text detail filter, it is enabled and only text details are shown`() {
        // Act
        viewModel.onFilterTextClicked()
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)

        Assert.assertEquals(1, filteredDetails.size)
        Assert.assertTrue(filteredDetails.contains(detail_2_jan_text))
    }

    @Test
    fun `when clicking the photo detail filter, it is enabled and only photo details are shown`() {
        // Act
        viewModel.onFilterPhotosClicked()
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)

        Assert.assertEquals(1, filteredDetails.size)
        Assert.assertTrue(filteredDetails.contains(detail_1_jan_photo))
    }

    @Test
    fun `when clicking the drawing detail filter, it is enabled and only drawing details are shown`() {
        // Act
        viewModel.onFilterDrawingClicked()
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)

        Assert.assertEquals(1, filteredDetails.size)
        Assert.assertTrue(filteredDetails.contains(detail_3_feb_drawing))
    }

    @Test
    fun `when clicking the audio detail filter, it is enabled and only  audio details are shown`() {
        // Act
        viewModel.onFilterAudioClicked()
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)
        println("events received $filteredDetails")

        Assert.assertEquals(1, filteredDetails.size)
        Assert.assertTrue(filteredDetails.contains(detail_4_july_audio))
    }

    @Test
    fun `when setting the start and end date filters, it is enabled and only details that in between those dates are shown`() {
        // Arrange
        viewModel.setDateRange(
            startDate = LocalDate.of(2020, Month.JANUARY, 1).atStartOfDay()
                .toInstant(ZoneOffset.of("+01:00")).toEpochMilli(),
            endDate = LocalDate.of(2020, Month.FEBRUARY, 1).atStartOfDay()
                .toInstant(ZoneOffset.of("+01:00")).toEpochMilli()
        )
        // Act
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)

        // Assert
        Assert.assertEquals(2, filteredDetails.size)
        Assert.assertTrue(filteredDetails.contains(detail_1_jan_photo))
        Assert.assertTrue(filteredDetails.contains(detail_2_jan_text))
    }

    @Test
    fun `when combining a text and a date filter, only text details from the given date range are shown`() {
        // Arrange
        viewModel.setDateRange(
            LocalDate.of(2020, Month.JANUARY, 1).atStartOfDay().toInstant(
                ZoneOffset.of("+01:00")
            ).toEpochMilli(), LocalDate.of(2020, Month.FEBRUARY, 1).atStartOfDay().toInstant(
                ZoneOffset.of("+01:00")
            ).toEpochMilli()
        )
        viewModel.onFilterTextClicked()

        // Act
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)

        // Assert
        Assert.assertEquals(1, filteredDetails.size)
        Assert.assertTrue(filteredDetails.contains(detail_2_jan_text))
    }

    @Test
    fun `when disabling a filter all details are shown again`() {
        // Arrange
        viewModel.onFilterTextClicked() // enable

        // Act
        viewModel.onFilterTextClicked() // disable
        val filteredDetails = TestUtils.getValue(viewModel.filteredDetails)

        // Assert
        Assert.assertEquals(4, filteredDetails.size)
    }
}

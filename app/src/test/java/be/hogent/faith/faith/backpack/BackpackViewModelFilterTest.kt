package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.faith.backpackScreen.DetailTypes.AUDIO_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.DRAW_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.PICTURE_DETAIL
import be.hogent.faith.faith.backpackScreen.DetailTypes.TEXT_DETAIL
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsMapContaining


class BackpackViewModelFilterTest {
    private lateinit var viewModel: BackpackViewModel


    @get:Rule
    val testRule = InstantTaskExecutorRule()
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>()

    private val details = listOf(
        mockk<AudioDetail>(), mockk<AudioDetail>(),
        mockk<TextDetail>(), mockk<TextDetail>(),
        mockk<PhotoDetail>(), mockk<PhotoDetail>(),
        mockk<DrawingDetail>(), mockk<DrawingDetail>()
    )

    @Before
    fun setUp() {

        every { getBackPackFilesDummyUseCase.getDetails() } returns details
        viewModel = BackpackViewModel(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            getBackPackFilesDummyUseCase
        )

        viewModel.details.getOrAwaitValue().toMutableList()


    }


    @Test
    fun backpackViewModel_showsAllWithoutFilter() {
        every { getBackPackFilesDummyUseCase.getDetails() } returns details
        assertThat(
            viewModel.getFilterDetailType().value,
            IsMapContaining.hasEntry(AUDIO_DETAIL, false)
        )
        assertThat(
            viewModel.getFilterDetailType().value,
            IsMapContaining.hasEntry(TEXT_DETAIL, false)
        )
        assertThat(
            viewModel.getFilterDetailType().value,
            IsMapContaining.hasEntry(DRAW_DETAIL, false)
        )
        assertThat(
            viewModel.getFilterDetailType().value,
            IsMapContaining.hasEntry(PICTURE_DETAIL, false)
        )



        assertEquals(8, viewModel.details.getOrAwaitValue().size)


    }

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }


}
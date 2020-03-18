package be.hogent.faith.faith.backpack

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.TestUtils
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
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsMapContaining

class BackpackViewModelFilterTest {
    private lateinit var viewModel: BackpackViewModel

    @get:Rule
    val testRule = InstantTaskExecutorRule()
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>()

    @Before
    fun setUp() {
        val details = listOf(
                mockk<AudioDetail>(), mockk<AudioDetail>(),
                mockk<TextDetail>(), mockk<TextDetail>(),
                mockk<PhotoDetail>(), mockk<PhotoDetail>(),
                mockk<DrawingDetail>(), mockk<DrawingDetail>()
        )

        every { getBackPackFilesDummyUseCase.getDetails() } returns details
        viewModel = BackpackViewModel(
                mockk(),
                mockk(),
                mockk(),
                mockk(),
                mockk(),
                getBackPackFilesDummyUseCase
        )
    }

    @Test
    fun backpackViewModel_showsAllWithoutFilter() {

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

        assertEquals(8, TestUtils.getValue(viewModel.details).size)
    }

    @Test
    fun backpackViewModel_filterOn1DetailType_AUDIO_DETAIL() {
        viewModel.setFilters(AUDIO_DETAIL)
        assertThat(
                viewModel.getFilterDetailType().value,
                IsMapContaining.hasEntry(AUDIO_DETAIL, true)
        )
        viewModel.details.observeForever {
            assertEquals(2, it.size)
            for (detail in it) {
                assertEquals(AudioDetail::class, detail.javaClass)
            }
        }
    }

    @Test
    fun backpackViewModel_filterOn2Types_AUDIO_DETAIL_TEXT_DETAIL() {
        viewModel.setFilters(AUDIO_DETAIL)
        viewModel.setFilters(TEXT_DETAIL)

        assertThat(
                viewModel.getFilterDetailType().value,
                IsMapContaining.hasEntry(AUDIO_DETAIL, true)
        )

        assertThat(
                viewModel.getFilterDetailType().value,
                IsMapContaining.hasEntry(TEXT_DETAIL, true)
        )
        viewModel.details.observeForever {
            assertEquals(4, it.size)
            for (detail in it) {
                var countAudioDetail = 0
                var countTextDetail = 0
                when (detail) {
                    is AudioDetail -> countAudioDetail++
                    is TextDetail -> countTextDetail++
                }
                assertEquals(2, countAudioDetail)
                assertEquals(2, countTextDetail)
            }
        }
    }
    /* @Test //Filter text is based on UUID actually, has to be changed to file title/description
     fun backpackViewModel_filterOnTextOnly(){

     }*/

    /*@Test //Filter text is based on UUID actually, has to be changed to file title/description
    fun backpackViewModel_filterOnTextAndType(){

    }*/
}
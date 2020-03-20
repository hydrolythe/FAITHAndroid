package be.hogent.faith.faith.backpack

import androidx.lifecycle.Observer
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.backpackScreen.BackpackViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

class SaveDetailFilenameTest {
    private val file = File("")
    private val filename = "ExistingFilename"
    private val detail = TextDetail(file, filename)
    private lateinit var viewModel: BackpackViewModel
    private lateinit var details: ArrayList<Detail>
    private val user = mockk<User>(relaxed = true)
    private val getBackPackFilesDummyUseCase = mockk<GetBackPackFilesDummyUseCase>(relaxed = true)

    @Before
    fun setUp() {
        viewModel = BackpackViewModel(
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            mockk(),
            getBackPackFilesDummyUseCase
        )
        details.add(detail)
        viewModel.addDetails(details)
    }

    @Test
    fun backpackViewModel_saveDetail_correctFilename() {
        // Arrange
        val file = File("")
        val fileName = "correctName"
        val detail = TextDetail(file)
        val errorObserver = mockk<Observer<Int>>(relaxed = true)
        viewModel.errorMessage.observeForever(errorObserver)

        // Act
        viewModel.onSaveClicked(fileName, user, detail)

        // Assert
        Assert.assertEquals(detail.fileName, fileName)
        verify { errorObserver wasNot called }
    }
}
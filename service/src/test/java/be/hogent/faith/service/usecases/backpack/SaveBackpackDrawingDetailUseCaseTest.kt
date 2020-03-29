package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.repository.BackpackRepository
import be.hogent.faith.storage.IStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SaveBackpackDrawingDetailUseCaseTest {
    private lateinit var saveBackpackDrawingDetailUseCase: SaveBackpackDetailUseCase
    private val scheduler: Scheduler = mockk()
    private val repository: BackpackRepository = mockk(relaxed = true)
    private val storageRepository: IStorageRepository = mockk(relaxed = true)
    private val user: User = mockk(relaxed = true)

    private val detail = mockk<DrawingDetail>()

    @Before
    fun setUp() {
        saveBackpackDrawingDetailUseCase =
            SaveBackpackDetailUseCase(
                repository,
                storageRepository,
                scheduler
            )
    }

    @Test
    fun saveDrawingUC_saveDrawingNormal_savedToStorage() {
        // Arrange
        every { repository.insertDetail(detail, user) } returns Maybe.fromSingle { detail }
        every {
            storageRepository.saveDetailFileForContainer(
                user.backpack,
                detail
            )
        } returns Single.just(detail)
        val params = SaveBackpackDetailUseCase.Params(user, detail)

        // Act
        saveBackpackDrawingDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()

        // Assert
        verify { repository.insertDetail(detail, user) }
    }
}
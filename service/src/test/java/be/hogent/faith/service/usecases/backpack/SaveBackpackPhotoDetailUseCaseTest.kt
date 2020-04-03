package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import be.hogent.faith.storage.IStorageRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class SaveBackpackPhotoDetailUseCaseTest {
    private lateinit var saveBackpackPhotoDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>
    private val scheduler: Scheduler = mockk()
    private val repository: IDetailContainerRepository<Backpack> = mockk(relaxed = true)
    private val storageRepository: IStorageRepository = mockk(relaxed = true)
    private val user: User = mockk(relaxed = true)

    private val detail = mockk<PhotoDetail>()

    @Before
    fun setUp() {
        saveBackpackPhotoDetailUseCase =
            SaveDetailsContainerDetailUseCase(
                repository,
                storageRepository,
                scheduler
            )
    }

    @Test
    fun savePhotoUC_savePhotoNormal_savedToStorage() {
        // Arrange
        every { repository.insertDetail(detail, user) } returns Maybe.fromSingle { detail }
        every { storageRepository.saveDetailFileForContainer(user.backpack, detail) } returns Single.just(detail)
        val params = SaveDetailsContainerDetailUseCase.Params(user, user.backpack, detail)

        // Act
        saveBackpackPhotoDetailUseCase.buildUseCaseObservable(params).test()
            .assertNoErrors()

        // Assert
        verify { repository.insertDetail(detail, user) }
    }
}
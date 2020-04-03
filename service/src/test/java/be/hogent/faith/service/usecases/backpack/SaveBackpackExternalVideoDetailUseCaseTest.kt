package be.hogent.faith.service.usecases.backpack

import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.service.repositories.IDetailContainerRepository
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase
import be.hogent.faith.storage.IStorageRepository
import io.mockk.mockk
import io.reactivex.Scheduler
import org.junit.Before

class SaveBackpackExternalVideoDetailUseCaseTest {
    private lateinit var saveBackpackExternalVideoDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>
    private val scheduler: Scheduler = mockk()
    private val storageRepository: IStorageRepository = mockk(relaxed = true)
    private val repository: IDetailContainerRepository<Backpack> = mockk(relaxed = true)

    private val detail = mockk<ExternalVideoDetail>()
    private val user = mockk<User>()

    @Before
    fun setUp() {
        saveBackpackExternalVideoDetailUseCase =
            SaveDetailsContainerDetailUseCase(
                        repository,
                    storageRepository,
                        scheduler
                )
    }

    /*  @Test
      fun savePhotoUC_savePhotoNormal_savedToStorage() {
          // TODO fix this
          // Arrange
          every { repository.insertDetail(detail,user) } returns
          val params = SaveBackpackExternalVideoDetailUseCase.Params(user,detail)

          // Act
          saveBackpackExternalVideoDetailUseCase.buildUseCaseObservable(params).test()
                  .assertNoErrors()
                  .assertComplete()

          // Assert
          verify { repository.insertDetail(detail,user) }
      }*/
}
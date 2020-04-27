package be.hogent.faith.service.usecases.backpack

// TODO: opstellen
/*
class SaveBackpackExternalVideoDetailUseCaseTest {
    private lateinit var saveBackpackExternalVideoDetailUseCase: SaveDetailsContainerDetailUseCase<Backpack>
    private val scheduler: Scheduler = mockk()
    private val storageRepository: IFileStorageRepository = mockk(relaxed = true)
    private val repository: IDetailContainerRepository<Backpack> = mockk(relaxed = true)

    private val detail = mockk<ExternalVideoDetail>()
    private val user = mockk<User>()

    @Before
    fun setUp() {
        saveBackpackExternalVideoDetailUseCase =
            SaveDetailsContainerDetailUseCase<Backpack>(
                repository,
                storageRepository,
                scheduler
            )
    }

     @Test
      fun savePhotoUC_savePhotoNormal_savedToStorage() {
          // Arrange
          every { repository.insertDetail(detail,user) } returns
          val params = SaveBackpackExternalVideoDetailUseCase.Params(user,detail)

          // Act
          saveBackpackExternalVideoDetailUseCase.buildUseCaseObservable(params).test()
                  .assertNoErrors()
                  .assertComplete()

          // Assert
          verify { repository.insertDetail(detail,user) }

}
 */
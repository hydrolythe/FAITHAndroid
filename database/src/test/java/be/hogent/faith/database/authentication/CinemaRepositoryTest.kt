package be.hogent.faith.database.repositories

// import be.hogent.faith.service.usecases.util.EncryptedDetailFactory

class CinemaRepositoryTest {
    /*
    private val cinemaDatabase = mockk<CinemaDatabase>(relaxed = true)
    private val userMapper = mockk<UserMapper>()
    private val detailMapper = mockk<DetailMapper>()
    private val containerMapper = mockk<DetailContainerMapper>()

    private val cinemaRepository =
        DetailContainerRepository<Cinema>(userMapper, detailMapper, containerMapper, cinemaDatabase)

    private val user = UserFactory.makeUser(0)
    private val userEntity = EntityFactory.makeUserEntity()
    private val detail = DetailFactory.makeRandomDetail()
    private val encryptedDetailEntity = EntityFactory.makeDetailEntity()
    private val encrypteddetail = EncryptedDetailFactory.makeRandomDetail()
    private val encryptedDetailEntitiesList = EntityFactory.makeDetailEntityList(2)
    private val encryptedDetailList =
        listOf(EncryptedDetailFactory.makeRandomDetail(), EncryptedDetailFactory.makeRandomDetail())

    @Test
    fun cinemaRepository_get_existingDetails_succeeds() {
        every { cinemaDatabase.getAll() } returns Flowable.just(
            encryptedDetailEntitiesList
        )
        stubMapperFromEntityList(encryptedDetailList, encryptedDetailEntitiesList)
        cinemaRepository.get()
            .test()
            .assertValue(encryptedDetailList)
    }

    @Test
    fun cinemaRepository_insertDetail_succeeds() {
        every {
            cinemaDatabase.insert(
                encryptedDetailEntity,
                userEntity
            )
        } returns Completable.complete()
        cinemaRepository.insertDetail(encrypteddetail, user)
            .test().assertComplete().assertNoErrors()
    }

    @Test
    fun cinemaRepository_deleteDetail_succeeds() {
        every { cinemaDatabase.delete(detail) } returns Completable.complete()
        cinemaRepository.deleteDetail(detail)
            .test().assertComplete().assertNoErrors()
    }

    private fun stubMapperFromEntityList(
        model: List<EncryptedDetail>,
        entity: List<EncryptedDetailEntity>
    ) {
        every { detailMapper.mapFromEntities(entity) } returns model
    }

     */
}
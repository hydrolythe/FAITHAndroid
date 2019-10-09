package be.hogent.faith.database.repositories

class EventRepositoryImplTest {
    /*
    private val database = mockk<EntityDatabase>(relaxed = true)
    private val detailMapper = mockk<DetailMapper>()
    private val eventMapper = mockk<EventMapper>()
    private val eventWithDetailsMapper = mockk<EventWithDetailsMapper>()

    private val eventRepository = EventRepositoryImpl(database, eventMapper, eventWithDetailsMapper, detailMapper)

    private val user = UserFactory.makeUser(0)
    private val eventWithDetails = EntityFactory.makeEventWithDetailsEntity(user.uuid, 2)
    private val eventUuid = eventWithDetails.eventEntity.uuid
    private val event = EventFactory.makeEvent(2)

    @Before
    fun setUp() {
        every { database.eventDao() } returns eventDao
        every { database.detailDao() } returns detailDao
    }

    @Test
    fun eventRepository_existingEvent_succeeds() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.just(
            eventWithDetails
        )
        stubMapperFromEntity(event, eventWithDetails)
        eventRepository.get(eventUuid)
            .test()
            .assertValue(event)
    }

    @Test
    fun eventRepository_nonExistingEvent_errors() {
        every { eventDao.getEventWithDetails(eventUuid) } returns Flowable.empty()

        eventRepository.get(eventUuid)
            .test()
            .assertNoValues()
    }

    @Test
    fun eventRepository_deleteEventCompletes() {
        every { eventDao.delete(eventWithDetails.eventEntity) } returns Completable.complete()
        stubMapperToEntity(event, eventWithDetails.eventEntity)
        eventRepository.delete(event, user).test().assertComplete()
    }

    @Test
    fun eventRepository_getAll_succeeds() {
        every { eventDao.getAllEventsWithDetails(user.uuid) } returns Flowable.just(listOf(eventWithDetails))
        stubMapperFromEntities(listOf(event), listOf(eventWithDetails))
        eventRepository.getAll(user).test().assertValue(listOf(event))
    }

    private fun stubMapperFromEntity(model: Event, entity: EventWithDetails) {
        every { eventWithDetailsMapper.mapFromEntity(entity) } returns model
    }

    private fun stubMapperFromEntities(models: List<Event>, entities: List<EventWithDetails>) {
        every { eventWithDetailsMapper.mapFromEntities(entities) } returns models
    }

    private fun stubMapperToEntity(model: Event, entity: EventEntity) {
        every { eventMapper.mapToEntity(model, user.uuid) } returns entity
    }

     */
}
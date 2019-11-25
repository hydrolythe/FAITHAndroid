package be.hogent.faith.storage

class StorageRepositoryTest {
/*
    val storageFactory = mockk<StorageFactory>(relaxed = true)
    private val localStorage = mockk<LocalStorage>()
    private val remoteStorage = mockk<RemoteStorage>()
    private val storageRepository = StorageRepository(storageFactory)

    @Test
    fun storeBitmap_WhenDeleted_ReturnsTrue() {
        val bitmapArg = slot<Bitmap>()
        val fileArg = slot<File>()
        val filenameArg = slot<String>()
        val bitmap = DataFactory.randomBitmap()
        val file = DataFactory.randomFile()
        val filename = DataFactory.randomString()
        every { storageFactory.getLocalStorage() } returns localStorage
    }

    @Test
    fun deleteFile_WhenDeleted_ReturnsTrue() {
        val fileArg = slot<File>()
        val file = DataFactory.randomFile()
        every { storageFactory.getLocalStorage() } returns localStorage
        every { localStorage.deleteFile(capture(fileArg)) } returns true

        val result = storageRepository.deleteFile(file)

        Assert.assertEquals(file.path, fileArg.captured.path)
        Assert.assertEquals(true, result)
    }

    @Test
    fun saveEventAudio_WhenSaved_ReturnsAFile() {
        val fileArg = slot<File>()
        val eventArg = slot<Event>()
        val file = DataFactory.randomFile()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)

        every { storageFactory.getLocalStorage() } returns localStorage
        every {
            localStorage.saveEventAudio(
                capture(fileArg),
                capture(eventArg)
            )
        } returns Single.just(file)

        val result = storageRepository.saveEventAudio(file, event).test()

        Assert.assertEquals(file.path, fileArg.captured.path)
        Assert.assertEquals(event, eventArg.captured)
        result.assertValue(file)
    }

    @Test
    fun saveEventPhoto_WhenSaved_ReturnsAFile() {
        val fileArg = slot<File>()
        val eventArg = slot<Event>()
        val file = DataFactory.randomFile()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)

        every { storageFactory.getLocalStorage() } returns localStorage
        every {
            localStorage.saveEventPhoto(
                capture(fileArg),
                capture(eventArg)
            )
        } returns Single.just(file)

        val result = storageRepository.saveEventPhoto(file, event).test()

        Assert.assertEquals(file.path, fileArg.captured.path)
        Assert.assertEquals(event, eventArg.captured)
        result.assertValue(file)
    }

    @Test
    fun saveText_WhenSaved_ReturnsAFile() {
        val textArg = slot<String>()
        val eventArg = slot<Event>()
        val file = DataFactory.randomFile()
        val event = EventFactory.makeEvent(nbrOfDetails = 2, hasEmotionAvatar = true)
        val text = DataFactory.randomString()

        every { storageFactory.getLocalStorage() } returns localStorage
        every { localStorage.saveText(capture(textArg), capture(eventArg)) } returns Single.just(
            file
        )

        val result = storageRepository.saveText(text, event).test()

        Assert.assertEquals(text, textArg.captured)
        Assert.assertEquals(event, eventArg.captured)
        result.assertValue(file)
    }

    @Test
    fun loadTextFromExistingDetail_ReturnsTheText() {
        val textDetailArg = slot<TextDetail>()
        val detail = DetailFactory.makeTextDetail()
        val text = DataFactory.randomString()

        every { storageFactory.getLocalStorage() } returns localStorage
        every { localStorage.loadTextFromExistingDetail(capture(textDetailArg)) } returns Single.just(
            text
        )

        val result = storageRepository.loadTextFromExistingDetail(detail).test()

        Assert.assertEquals(detail, textDetailArg.captured)
        result.assertValue(text)
    }

    @Test
    fun overwriteTextDetail_ReturnsCompletable() {
        val textArg = slot<String>()
        val textDetailArg = slot<TextDetail>()
        val detail = DetailFactory.makeTextDetail()
        val text = DataFactory.randomString()

        every { storageFactory.getLocalStorage() } returns localStorage
        every { localStorage.overwriteTextDetail(capture(textArg), capture(textDetailArg)) } returns Completable.complete()

        val result = storageRepository.overwriteTextDetail(text, detail).test()

        Assert.assertEquals(detail, textDetailArg.captured)
        Assert.assertEquals(text, textArg.captured)
        result.assertComplete()
    }

    @Test
    fun saveEventEmotionAvatar_ReturnsFile() {
        val eventArg = slot<Event>()
        val event = EventFactory.makeEvent(hasEmotionAvatar = true)
        val file = DataFactory.randomFile()

        every { storageFactory.getRemoteDataStore() } returns remoteStorage
        every { remoteStorage.saveEventEmotionAvatar(capture(eventArg)) } returns Single.just(file)

        val result = storageRepository.saveEventEmotionAvatar(event).test()

        Assert.assertEquals(event, eventArg.captured)
        result.assertValue(file)
    }

    @Test
    fun saveDetailFile_ReturnsFile() {
        val eventArg = slot<Event>()
        val detailArg = slot<Detail>()
        val event = EventFactory.makeEvent(hasEmotionAvatar = true, nbrOfDetails = 1)
        val detail = DetailFactory.makeRandomDetail()
        val file = DataFactory.randomFile()

        every { storageFactory.getRemoteDataStore() } returns remoteStorage
        every { remoteStorage.saveDetailFile(capture(eventArg), capture(detailArg)) } returns Single.just(file)

        val result = storageRepository.saveDetailFile(event, detail).test()

        Assert.assertEquals(event, eventArg.captured)
        Assert.assertEquals(detail, detailArg.captured)
        result.assertValue(file)
    }

    @Test
    fun moveFilesFromRemoteStorageToLocalStorage_ReturnsCompletable() {
        val eventArg = slot<Event>()
        val event = EventFactory.makeEvent(hasEmotionAvatar = true, nbrOfDetails = 1)

        every { storageFactory.getRemoteDataStore() } returns remoteStorage
        every { remoteStorage.moveFileFromRemoteStorageToLocalStorage(capture(eventArg)) } returns Completable.complete()

        val result = storageRepository.moveFilesFromRemoteStorageToLocalStorage(event).test()

        Assert.assertEquals(event, eventArg.captured)
        result.assertComplete()
    }
    */
}

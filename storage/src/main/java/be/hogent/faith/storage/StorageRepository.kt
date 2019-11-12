package be.hogent.faith.storage

import android.content.Context
import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.util.UUID

const val EMOTION_AVATAR_FILENAME = "emotionAvatar"

/**
 * Repository providing access to both the internal and external storage.
 * It decides which one will be used based on the user's settings.
 *
 * TODO: Currently only supports internal storage.
 */
const val TEXT_EXTENSION = "txt"

class StorageRepository(private val context: Context) : StorageRepositoryInterface {
    override fun storePhotoDetailWithEvent(photoDetail: PhotoDetail, event: Event): Completable {
        val saveFile = File(getEventPhotoDirectory(event), photoDetail.uuid.toString())
        return moveFile(photoDetail.file, saveFile)
            .doOnComplete {
                photoDetail.file = saveFile
            }
    }

    override fun storeTextTemporarily(text: String): Single<File> {
        return Single.fromCallable {
            val saveDirectory = File(context.cacheDir, "text")
            saveDirectory.mkdirs()
            val saveFile = File(saveDirectory, UUID.randomUUID().toString())
            saveFile.writeText(text)
            saveFile
        }
    }

    override fun storeTextDetailWithEvent(textDetail: TextDetail, event: Event): Completable {
        val saveFile = File(getEventTextDirectory(event), textDetail.uuid.toString())
        return moveFile(textDetail.file, saveFile)
            .doOnComplete {
                textDetail.file = saveFile
            }
    }

    /**
     * Stores the bitmap in the given file
     *
     * @return a Single<File> pointing to the file where the bitmap was saved
     */
    private fun storeBitmap(bitmap: Bitmap, file: File): Completable {
        return Completable.fromCallable {
            require(!file.isDirectory) {
                "Must provide a file, not a directory, in which to store the bitmap." +
                        "${file.path} is a directory."
            }
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    /**
     * Stores the bitmap in the given file in the given folder.
     *
     * @return a Single<File> pointing to the file where the bitmap was saved
     */
    private fun storeBitmap(bitmap: Bitmap, folder: File, fileName: String): Single<File> {
        require(folder.isDirectory) {
            "Must provide a directory, not a file, in which to store the bitmap.\n" +
                    "${folder.path} is not a directory."
        }
        require(!fileName.isNullOrBlank()) { "Empty filenames are not allowed when storing bitmaps" }

        val saveFile = File(folder, fileName)
        return storeBitmap(bitmap, saveFile).andThen(Single.just(saveFile))
    }

    private fun getEventPhotoDirectory(event: Event): File {
        val imageDirectory = File(getEventDirectory(event), "photos")
        imageDirectory.mkdirs()
        return imageDirectory
    }

    private fun getEventDrawingDirectory(event: Event): File {
        val audioDir = File(getEventDirectory(event), "drawings")
        audioDir.mkdirs()
        return audioDir
    }

    private fun getEventAudioDirectory(event: Event): File {
        val audioDir = File(getEventDirectory(event), "audio")
        audioDir.mkdirs()
        return audioDir
    }

    private fun getEventDirectory(event: Event): File {
        val eventDir = File(context.filesDir, "events/${event.uuid}")
        eventDir.mkdirs()
        return eventDir
    }

    private fun getEventTextDirectory(event: Event): File {
        val textDir = File(context.filesDir, "text/${event.uuid}")
        textDir.mkdirs()
        return textDir
    }

    fun saveEventAudio(tempStorageFile: File, event: Event): Single<File> {
        return Single.fromCallable {
            val saveFile = File(getEventAudioDirectory(event), createSaveFileName())
            moveFile(tempStorageFile, saveFile)
            saveFile
        }
    }

    /**
     * Saves a given [DrawingDetail] to permanent storage.
     * This will change the [detail]'s file property to the new location.
     * It will be found in appDir/events/[eventUuid]/detailUuid
     */
    override fun storeDrawingDetailWithEvent(detail: DrawingDetail, event: Event): Completable {
        val saveFile = File(getEventDrawingDirectory(event), detail.uuid.toString())
        return moveFile(detail.file, saveFile)
            .doOnComplete {
                detail.file = saveFile
            }
    }

    fun saveEventPhoto(tempStorageFile: File, event: Event): Single<File> {
        return Single.fromCallable {
            val saveFile = File(getEventPhotoDirectory(event), createSaveFileName())
            moveFile(tempStorageFile, saveFile)
            saveFile
        }
    }

    fun saveEventEmotionAvatar(bitmap: Bitmap, event: Event): Single<File> {
        return storeBitmap(
            bitmap,
            getEventDirectory(event),
            EMOTION_AVATAR_FILENAME
        )
    }

    /**
     * Returns a saveFile with the name in the following format:
     * day_month_year_hour_minute_second_millis
     */
    private fun createSaveFileName(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("d_M_y_k_m_s_A"))
    }

    private fun moveFile(sourceFile: File, destinationFile: File): Completable {
        return Completable.fromCallable {
            sourceFile.copyTo(target = destinationFile, overwrite = true)
            sourceFile.delete()
        }
    }

    /**
     *  Writes a String to a text file
     *
     * @param text the String
     * @param event the [Event] this text will be added to as a detail (not by this function).
     *          Used to store the text in a folder specific for the event.
     */
    fun saveText(text: String, event: Event): Single<File> {
        return Single.fromCallable {
            val storedFile =
                File(getEventTextDirectory(event), "${createSaveFileName()}.$TEXT_EXTENSION")
            storedFile.writeText(text)
            storedFile
        }
    }

    fun overwriteExistingDrawingDetail(bitmap: Bitmap, drawingDetail: DrawingDetail): Completable {
        return storeBitmap(bitmap, drawingDetail.file)
    }

    override fun storeBitmapTemporarily(bitmap: Bitmap): Single<File> {
        val saveDirectory = File(context.cacheDir, "pictures")
        saveDirectory.mkdirs()
        return storeBitmap(bitmap, saveDirectory, UUID.randomUUID().toString())
    }

    fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return Single.fromCallable {
            val readString = textDetail.file.readText()
            readString
        }
    }

    fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return Completable.fromCallable {
            existingDetail.file.writeText(text)
        }
    }
}
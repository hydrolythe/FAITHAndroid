package be.hogent.faith.storage.localstorage

import android.content.Context
import android.graphics.Bitmap
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.storage.StoragePathProvider
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.util.UUID

class TemporaryStorageRepository(
    private val context: Context,
    private val pathProvider: StoragePathProvider
) : ITemporaryStorageRepository {

    /**
     * Stores a detail in its event's folder, and sets the the details path to that location
     */
    override fun storeDetailWithEvent(detail: Detail, event: Event): Completable {
        val saveFile =
            with(pathProvider) { temporaryStoragePath(getDetailPath(detail, event)) }
        return moveFile(detail.file, saveFile)
            .andThen(
                Completable.fromCallable {
                    detail.file = saveFile // relativePath
                    Unit
                }
            )
            .doOnComplete {
                Timber.d("Saved detail $detail with event ${event.uuid}")
            }
    }

    override fun storeTextTemporarily(text: String): Single<File> {
        return Single.fromCallable {
            val saveDirectory = File(context.cacheDir, "text")
            saveDirectory.mkdirs()
            val saveFile = File(saveDirectory, UUID.randomUUID().toString())
            saveFile.writeText(text)
            Timber.d("Saved text $text temporarily at $saveFile")
            saveFile
        }
    }

    override fun storeBitmapTemporarily(bitmap: Bitmap): Single<File> {
        val saveDirectory = File(context.cacheDir, "pictures")
        saveDirectory.mkdirs()
        return storeBitmap(bitmap, saveDirectory, UUID.randomUUID().toString())
    }

    /**
     * Stores the bitmap in the given file
     */
    private fun storeBitmap(bitmap: Bitmap, file: File): Completable {
        return Completable.fromCallable {
            require(!file.isDirectory) {
                "Must provide a file, not a directory, in which to store the bitmap. ${file.path} is a directory."
            }
            file.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            Timber.d("Saved bitmap temporarily at $file")
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
        require(!fileName.isBlank()) { "Empty filenames are not allowed when storing bitmaps" }

        val saveFile = File(folder, fileName)
        return storeBitmap(bitmap, saveFile).andThen(Single.just(saveFile))
    }

    private fun moveFile(sourceFile: File, destinationFile: File): Completable {
        return Completable.fromCallable {
            sourceFile.copyTo(target = destinationFile, overwrite = true)
            sourceFile.delete()
        }
    }

    override fun overwriteExistingDrawingDetail(
        bitmap: Bitmap,
        drawingDetail: DrawingDetail
    ): Completable {
        return storeBitmap(bitmap, drawingDetail.file)
    }

    override fun loadTextFromExistingDetail(textDetail: TextDetail): Single<String> {
        return Single.fromCallable {
            val readString = textDetail.file.readText()
            readString
        }
    }

    override fun overwriteTextDetail(text: String, existingDetail: TextDetail): Completable {
        return Completable.fromCallable {
            existingDetail.file.writeText(text)
        }
    }

    override fun isFilePresent(detail: Detail, event: Event): Boolean {
        return with(pathProvider) { temporaryStoragePath(getDetailPath(detail, event)).exists() }
    }

    override fun isEmotionAvatarPresent(event: Event): Boolean {
        return with(pathProvider) { temporaryStoragePath(getEmotionAvatarPath(event)).exists() }
    }
}
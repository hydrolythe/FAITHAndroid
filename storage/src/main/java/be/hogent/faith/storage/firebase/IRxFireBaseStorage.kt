package be.hogent.faith.storage.firebase

import android.net.Uri
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.Single
import java.io.File

interface IRxFireBaseStorage {

    fun getFile(storageReference: StorageReference, destinationFile: File):
            Single<FileDownloadTask.TaskSnapshot>

    fun putFile(
        storageReference: StorageReference,
        uri: Uri
    ): Single<UploadTask.TaskSnapshot>
}
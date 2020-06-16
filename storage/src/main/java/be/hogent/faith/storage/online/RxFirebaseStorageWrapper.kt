package be.hogent.faith.storage.online

import android.net.Uri
import be.hogent.faith.database.rxfirebase3.RxFirebaseStorage
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File

/**
 * Wrapper for [RxFirebaseStorage]. That's a static class so it can't be injected, meaning it also
 * can't be mocked.
 * Use this wrapper as a dependency so you can also mock it during testing.
 */
class RxFirebaseStorageWrapper : IRxFireBaseStorage {
    override fun getFile(
        storageReference: StorageReference,
        destinationFile: File
    ): Single<FileDownloadTask.TaskSnapshot> {
        return RxFirebaseStorage.getFile(storageReference, destinationFile)
    }

    override fun putFile(
        storageReference: StorageReference,
        uri: Uri
    ): Single<UploadTask.TaskSnapshot> {
        return RxFirebaseStorage.putFile(storageReference, uri)
    }

    override fun delete(storageReference: StorageReference): Completable {
        return RxFirebaseStorage.delete(storageReference)
    }
}
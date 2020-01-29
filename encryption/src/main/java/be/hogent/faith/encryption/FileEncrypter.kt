package be.hogent.faith.encryption

import be.hogent.faith.storage.encryption.IFileEncrypter
import be.hogent.faith.util.withSuffix
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.streamingaead.StreamingAeadFactory
import io.reactivex.Completable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel

private const val CHUNK_SIZE = 16_000 * Byte.SIZE_BYTES
private const val ORIGINAL_FILE_SUFFIX = "_encrypted"

// This dummy associatedData is required  to open a decryptingChannel
// Passing null (as you can do with a regular Aead) results in a nullpointer when oping the
// decryptionChannel.
private val associatedData = "aad".toByteArray()

class FileEncrypter(
    keysetHandle: KeysetHandle
) : IFileEncrypter {

    private val streamingAead = StreamingAeadFactory.getPrimitive(keysetHandle)

    /**
     * Encrypts the contents of the given [file].
     */
    override fun encrypt(file: File): Completable {
        return Completable.fromCallable {
            val originalBackup = File(file.path.withSuffix(ORIGINAL_FILE_SUFFIX))
            file.copyTo(originalBackup)

            val cipherTextDestination: FileChannel = FileOutputStream(file).channel
            val encryptingChannel: WritableByteChannel =
                streamingAead.newEncryptingChannel(cipherTextDestination, associatedData)

            val buffer: ByteBuffer = ByteBuffer.allocate(CHUNK_SIZE)
            val fileInputStream: InputStream = FileInputStream(originalBackup)

            while (fileInputStream.available() > 0) {
                fileInputStream.read(buffer.array())
                encryptingChannel.write(buffer)
            }

            encryptingChannel.close()
            fileInputStream.close()
        }
    }

    /**
     * Decrypts the contents of the given [file].
     */
    override fun decrypt(file: File): Completable {
        return Completable.fromCallable {
            val originalBackup = File(file.path.removeSuffix(ORIGINAL_FILE_SUFFIX))
            file.copyTo(originalBackup)

            val cipherTextSource =
                FileInputStream(originalBackup).channel
            val decryptingChannel =
                streamingAead.newDecryptingChannel(cipherTextSource, associatedData)

            val out: OutputStream = FileOutputStream(file)
            val buffer = ByteBuffer.allocate(CHUNK_SIZE)
            var cnt: Int
            do {
                buffer.clear()
                cnt = decryptingChannel.read(buffer)
                out.write(buffer.array())
            } while (cnt > 0)

            decryptingChannel.close()
            out.close()
        }
    }
}

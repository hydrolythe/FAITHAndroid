package be.hogent.faith.encryption

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
private const val ORIGINAL_FILE_SUFFIX = "_temp"

// This dummy associatedData is required to open a decryptingChannel
// Passing null (as you can do with a regular Aead) results in a nullpointer when oping the
// decryptionChannel.
private val associatedData = "aad".toByteArray()

class FileEncrypter(
    keysetHandle: KeysetHandle
) {

    private val streamingAead = StreamingAeadFactory.getPrimitive(keysetHandle)

    /**
     * Encrypts the contents of the given [file].
     */
    fun encrypt(file: File): Completable {
        return Completable.fromCallable {
            val originalBackup = createBackupOfOriginal(file)

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
            // Remove temp file
            removeBackup(originalBackup)
        }
    }

    /**
     * Decrypts the contents of the given [file].
     */
    fun decrypt(file: File): Completable {
        return Completable.fromCallable {
            val cipherTextFile = createBackupOfOriginal(file)

            val cipherTextSource =
                FileInputStream(cipherTextFile).channel
            val decryptingChannel =
                streamingAead.newDecryptingChannel(cipherTextSource, associatedData)

            val out: OutputStream = FileOutputStream(file, false)
            val buffer = ByteBuffer.allocate(CHUNK_SIZE)
            var cnt: Int
            do {
                buffer.clear()
                cnt = decryptingChannel.read(buffer)
                out.write(buffer.array())
            } while (cnt > 0)

            decryptingChannel.close()
            out.close()

            removeBackup(cipherTextFile)
        }
    }

    private fun removeBackup(originalBackup: File) = originalBackup.delete()

    private fun createBackupOfOriginal(original: File): File {
        val originalBackup = File(original.path.withSuffix(ORIGINAL_FILE_SUFFIX))
        original.copyTo(originalBackup)
        return originalBackup
    }
}

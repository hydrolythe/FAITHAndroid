package be.hogent.faith.encryption

import be.hogent.faith.util.withSuffix
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.streamingaead.StreamingAeadFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel

private const val CHUNK_SIZE = 1024
private const val ENCRYPTED_FILE_SUFFIX = "_encrypted"
private const val DECRYPTED_FILE_SUFFIX = "_decrypted"

// This dummy associatedData is required to open a decryptingChannel
// Passing null (as you can do with a regular Aead) results in a nullpointer when oping the
// decryptionChannel.
private val associatedData = "aad".toByteArray()

class FileEncryptionService {

    /**
     * Writes a encrypted version of the given [plainTextFile] the returning [File].
     */
    fun encrypt(plainTextFile: File, streamingDEK: KeysetHandle): Single<File> {
        return Single.fromCallable {
            val streamingAead = StreamingAeadFactory.getPrimitive(streamingDEK)
            val encryptedFile = File(plainTextFile.path.withSuffix(ENCRYPTED_FILE_SUFFIX))

            val cipherTextDestination: FileChannel = FileOutputStream(encryptedFile).channel
            val encryptingChannel: WritableByteChannel =
                streamingAead.newEncryptingChannel(cipherTextDestination, associatedData)

            val plainTextChannel = RandomAccessFile(plainTextFile, "r").channel
            val buffer: ByteBuffer = createBuffer(plainTextChannel)

            var bytesRead = plainTextChannel.read(buffer)
            while (bytesRead > 0) {
                buffer.flip()
                encryptingChannel.write(buffer)
                buffer.clear()
                bytesRead = plainTextChannel.read(buffer)
            }
            encryptingChannel.close()
            plainTextChannel.close()

            encryptedFile
        }
    }

    /**
     * Writes a decrypted version of the given [cypherTextFile] to the specified [destinationFile].
     */
    fun decrypt(
        cypherTextFile: File,
        streamingDEK: KeysetHandle,
        destinationFile: File
    ): Completable {
        return Completable.fromCallable {
            val streamingAead = StreamingAeadFactory.getPrimitive(streamingDEK)

            val cipherTextChannel = FileInputStream(cypherTextFile).channel
            val decryptingChannel =
                streamingAead.newDecryptingChannel(cipherTextChannel, associatedData)

            destinationFile.parentFile.mkdirs()
            val plainTextChannel = FileOutputStream(destinationFile, false).channel
            val buffer = createBuffer(cipherTextChannel)

            var bytesRead = decryptingChannel.read(buffer)
            while (bytesRead > 0) {
                buffer.flip()
                plainTextChannel.write(buffer)
                buffer.clear()
                bytesRead = decryptingChannel.read(buffer)
            }

            decryptingChannel.close()
            plainTextChannel.close()
        }
    }

    /**
     * Writes a decrypted version of the given [cypherTextFile] to a File, and returns it.
     */
    fun decrypt(cypherTextFile: File, streamingDEK: KeysetHandle): Single<File> {
        val plainTextFile = File(cypherTextFile.path.withSuffix(DECRYPTED_FILE_SUFFIX))
        return decrypt(cypherTextFile, streamingDEK, plainTextFile)
            .andThen(Single.just(plainTextFile))
    }

    private fun createBuffer(channel: FileChannel): ByteBuffer {
        var bufferSize = CHUNK_SIZE
        if (CHUNK_SIZE > channel.size()) {
            bufferSize = channel.size().toInt()
        }
        return ByteBuffer.allocate(bufferSize)
    }
}

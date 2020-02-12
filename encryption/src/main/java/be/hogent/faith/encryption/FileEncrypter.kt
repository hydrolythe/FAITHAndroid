package be.hogent.faith.encryption

import be.hogent.faith.util.withSuffix
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.streamingaead.StreamingAeadFactory
import io.reactivex.Completable
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

class FileEncrypter(
    keysetHandle: KeysetHandle
) {

    private val streamingAead = StreamingAeadFactory.getPrimitive(keysetHandle)

    /**
     * Encrypts the contents of the given [plainTextFile].
     */
    fun encrypt(plainTextFile: File): Completable {
        return Completable.fromCallable {
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

            encryptedFile.copyTo(plainTextFile, overwrite = true)
            encryptedFile.delete()
        }
    }

    /**
     * Decrypts the contents of the given [cypherTextFile].
     */
    fun decrypt(cypherTextFile: File): Completable {
        return Completable.fromCallable {
            val plainTextFile = File(cypherTextFile.path.withSuffix(DECRYPTED_FILE_SUFFIX))

            val cipherTextChannel = FileInputStream(cypherTextFile).channel
            val decryptingChannel =
                streamingAead.newDecryptingChannel(cipherTextChannel, associatedData)

            val plainTextChannel = FileOutputStream(plainTextFile, false).channel
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

            plainTextFile.copyTo(cypherTextFile, overwrite = true)
            plainTextFile.delete()
        }
    }

    private fun createBuffer(channel: FileChannel): ByteBuffer {
        var bufferSize = CHUNK_SIZE
        if (CHUNK_SIZE > channel.size()) {
            bufferSize = channel.size().toInt()
        }
        return ByteBuffer.allocate(bufferSize)
    }
}

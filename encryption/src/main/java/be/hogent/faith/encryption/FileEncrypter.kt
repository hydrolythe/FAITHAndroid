package be.hogent.faith.encryption

import be.hogent.faith.storage.encryption.IFileEncrypter
import be.hogent.faith.util.withSuffix
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.streamingaead.StreamingAeadFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel

private const val CHUNK_SIZE = 16_000 * Byte.SIZE_BYTES
private const val ENCRYPTED_FILE_SUFFIX = "_encrypted"

// This dummy associatedData is required  to open a decryptingChannel
// Passing null (as you can do with a regular Aead) results in a nullpointer when oping the
// decryptionChannel.
private val associatedData = "aad".toByteArray()

class FileEncrypter(
    keysetHandle: KeysetHandle
) : IFileEncrypter {

    private val streamingAead = StreamingAeadFactory.getPrimitive(keysetHandle)

    /**
     * Encrypt the contents of a given [file].
     *
     * @return Returns the [File] where the encrypted version is stored
     */
    override fun encrypt(file: File): File {
        val encryptedFile = File(file.path.withSuffix(ENCRYPTED_FILE_SUFFIX))

        val cipherTextDestination: FileChannel = FileOutputStream(encryptedFile).channel
        val encryptingChannel: WritableByteChannel =
            streamingAead.newEncryptingChannel(cipherTextDestination, associatedData)

        val buffer: ByteBuffer = ByteBuffer.allocate(CHUNK_SIZE)
        val fileInputStream: InputStream = FileInputStream(file)

        while (fileInputStream.available() > 0) {
            fileInputStream.read(buffer.array())
            encryptingChannel.write(buffer)
        }

        encryptingChannel.close()
        fileInputStream.close()

        return encryptedFile
    }

    /**
     * Replaces the contents of an encrypted file with a decrypted version
     * @return the [File] where the decrypted version is stored
     */
    override fun decrypt(file: File): File {
        val decryptedFile = File(file.path.removeSuffix(ENCRYPTED_FILE_SUFFIX))
        val cipherTextSource =
            FileInputStream(file).channel
        val decryptingChannel = streamingAead.newDecryptingChannel(cipherTextSource, associatedData)

        val out: OutputStream = FileOutputStream(decryptedFile)
        val buffer = ByteBuffer.allocate(CHUNK_SIZE)
        var cnt: Int
        do {
            buffer.clear()
            cnt = decryptingChannel.read(buffer)
            out.write(buffer.array())
        } while (cnt > 0)

        decryptingChannel.close()
        out.close()
        return decryptedFile
    }
}

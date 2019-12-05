package be.hogent.faith.encryption.encryptionService

import com.google.cloud.kms.v1.CryptoKeyName
import com.google.cloud.kms.v1.CryptoKeyPathName
import com.google.cloud.kms.v1.EncryptResponse
import com.google.cloud.kms.v1.KeyManagementServiceClient
import com.google.protobuf.ByteString


const val KMS_ENDPOINT = "https://cloudkms.googleapis.com/v1/"
const val PROJECT_ID = "faith-febb-208508"
const val LOCATION = "global"
const val KEYRING_NAME = "test"
const val KEY_NAME = "testkey"

class KMSApi {

    private val client = KeyManagementServiceClient.create()
    private val resourceName = CryptoKeyName.format(PROJECT_ID, LOCATION, KEYRING_NAME, KEY_NAME)

    fun encrypt(plainText: ByteArray): ByteArray {
        val keyManagementServiceClient = KeyManagementServiceClient.create()
        val name =
            CryptoKeyPathName.of(PROJECT_ID, LOCATION, KEYRING_NAME, KEY_NAME)
        val plaintext = ByteString.copyFromUtf8("")
        val response: EncryptResponse =
            keyManagementServiceClient.encrypt(name.toString(), plaintext)
        return response.ciphertext.toByteArray()
    }
}

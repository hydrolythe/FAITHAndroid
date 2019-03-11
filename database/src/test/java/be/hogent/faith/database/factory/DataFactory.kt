package be.hogent.faith.database.factory

import org.threeten.bp.LocalDateTime
import java.io.File
import java.nio.charset.Charset
import java.util.Random
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom

object DataFactory {

    fun randomFile(): File {
        val array = ByteArray(7) // length is bounded by 7
        Random().nextBytes(array)
        val generatedString = String(array, Charset.forName("UTF-8"))
        return File("testDirectory/$generatedString")
    }

    fun randomUID(): UUID {
        return UUID.randomUUID()
    }

    fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, 1000 + 1)
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }

    fun randomDateTime(): LocalDateTime {
        val year = ThreadLocalRandom.current().nextInt(1990, 2020)
        val month = ThreadLocalRandom.current().nextInt(1, 13)
        val day = ThreadLocalRandom.current().nextInt(1, 29)
        return LocalDateTime.of(year, month, day, 7, 33)
    }
}
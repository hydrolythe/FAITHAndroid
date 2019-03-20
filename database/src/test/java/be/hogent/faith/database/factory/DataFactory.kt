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

    /**
     * Returns a random integer between 2 given boundaries.
     * Defaults to 0 and 1000
     */
    fun randomInt(from: Int = 0, to: Int = 1000): Int {
        return ThreadLocalRandom.current().nextInt(from, to + 1)
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }

    fun randomHour(): Int {
        return randomInt(0, 23)
    }

    fun randomMinute(): Int {
        return randomInt(0, 59)
    }

    fun randomDateTime(): LocalDateTime {
        val year = ThreadLocalRandom.current().nextInt(1990, 2020)
        val month = ThreadLocalRandom.current().nextInt(1, 13)
        val day = ThreadLocalRandom.current().nextInt(1, 29)
        return LocalDateTime.of(year, month, day, randomHour(), randomMinute())
    }
}
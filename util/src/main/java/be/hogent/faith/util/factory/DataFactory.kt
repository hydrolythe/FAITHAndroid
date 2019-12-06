package be.hogent.faith.util.factory

import android.graphics.Bitmap
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.io.File
import java.nio.charset.Charset
import java.util.Random
import java.util.UUID

object DataFactory {

    fun randomFile(): File {
        val array = ByteArray(7) // length is bounded by 7
        Random().nextBytes(array)
        val generatedString = String(array, Charset.forName("UTF-8"))
        return File("testDirectory/$generatedString")
    }

    fun randomBitmap(): Bitmap {
        return Bitmap.createBitmap(randomInt(), randomInt(), Bitmap.Config.ARGB_8888)
    }

    fun randomUUID(): UUID {
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
        return (from until to).random()
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }

    fun randomTime(): LocalTime {
        return LocalTime.of(randomHour(), randomMinute())
    }

    fun randomDate(): LocalDate {
        val year = (1990 until 2018).random()
        val month = (1 until 12).random()
        val day = (1 until 29).random()

        return LocalDate.of(year, month, day)
    }

    fun randomHour(): Int {
        return randomInt(0, 23)
    }

    fun randomMinute(): Int {
        return randomInt(0, 59)
    }

    fun randomDateTime(): LocalDateTime {
        return LocalDateTime.of(randomDate(), randomTime())
    }
}
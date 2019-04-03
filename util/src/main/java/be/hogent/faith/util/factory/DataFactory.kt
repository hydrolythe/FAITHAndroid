package be.hogent.faith.util.factory

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

    fun randomUUID(): UUID {
        return UUID.randomUUID()
    }

    fun randomString(): String {
        return UUID.randomUUID().toString()
    }

    fun randomInt(): Int {
        return (0 until 1000 + 1).random()
    }

    fun randomLong(): Long {
        return randomInt().toLong()
    }

    fun randomBoolean(): Boolean {
        return Math.random() < 0.5
    }

    fun randomTime(): LocalTime {
        val hour = (0 until 23).random()
        val minutes = (0 until 59).random()
        return LocalTime.of(hour, minutes)
    }

    fun randomDate(): LocalDate {
        val year = (1990 until 2018).random()
        val month = (1 until 12).random()
        val day = (1 until 29).random()

        return LocalDate.of(year, month, day)
    }

    fun randomDateTime(): LocalDateTime {
        return LocalDateTime.of(randomDate(), randomTime())
    }
}
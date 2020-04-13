package be.hogent.faith.domain

import be.hogent.faith.domain.models.Film
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import java.io.File

class FilmTest {
    @Test
    fun `Film constructor correctly sets attributes`() {
        val date = LocalDateTime.of(2010, Month.OCTOBER, 28, 8, 33)
        val title = "filmtitle"
        val file = mockk<File>()
        val newFilm = Film(title, date, file)

        Assert.assertEquals(title, newFilm.title)
        Assert.assertEquals(date, newFilm.dateTime)
        Assert.assertEquals(file, newFilm.file)
    }
}
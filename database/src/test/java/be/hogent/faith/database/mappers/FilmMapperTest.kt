package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.models.FilmEntity
import be.hogent.faith.domain.models.Film

import be.hogent.faith.util.factory.FilmFactory
import org.junit.Assert
import org.junit.Test

class FilmMapperTest {
    private val film = FilmFactory.makeFilm()
    private val filmMapper = FilmMapper

    @Test
    fun `should map to Film when FilmEntity is given`() {
        val entity = EntityFactory.makeFilmEntity()
        val model = filmMapper.mapFromEntity(entity)
        assertEqualData(entity, model)
    }

    @Test
    fun `should map to FilmEntity when Film is given`() {
        val model = FilmFactory.makeFilm()
        val entity = filmMapper.mapToEntity(model)
        assertEqualData(entity, model)
    }

    private fun assertEqualData(
        entity: FilmEntity,
        model: Film
    ) {
        Assert.assertEquals(entity.uuid, model.uuid.toString())
        Assert.assertEquals(entity.file, FileConverter().toString(model.file))
        Assert.assertEquals(entity.title, model.title)
    }
}
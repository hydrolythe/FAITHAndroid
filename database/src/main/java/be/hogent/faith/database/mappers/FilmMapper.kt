package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.FilmEntity
import be.hogent.faith.domain.models.Film
import java.util.UUID

object FilmMapper : Mapper<FilmEntity, Film> {

    override fun mapFromEntity(entity: FilmEntity): Film {
        return Film(
            title = entity.title,
            dateTime = LocalDateTimeConverter().toDate(entity.dateTime),
            file = entity.file.let { FileConverter().toFile(it) },
            uuid = UUID.fromString(entity.uuid)
        )
    }

    override fun mapToEntity(model: Film): FilmEntity {
        return FilmEntity(
            dateTime = LocalDateTimeConverter().toString(model.dateTime),
            title = model.title,
            file = model.file.path,
            uuid = model.uuid.toString()
        )
    }

    override fun mapFromEntities(entities: List<FilmEntity>): List<Film> {
        return entities.map(this::mapFromEntity)
    }

    override fun mapToEntities(models: List<Film>): List<FilmEntity> {
        return models.map { mapToEntity(it) }
    }
}
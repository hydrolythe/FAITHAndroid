package be.hogent.faith.database.mappers

import be.hogent.faith.database.converters.FileConverter
import be.hogent.faith.database.converters.LocalDateTimeConverter
import be.hogent.faith.database.models.DetailEntity
import be.hogent.faith.database.models.DetailType
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.FilmDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import org.threeten.bp.LocalDateTime
import java.util.UUID

/**
 * Maps details of a given event.
 *
 * The event is required because we need its uuid to map the foreign key relationship.
 */

object DetailMapper : Mapper<DetailEntity, Detail> {
    override fun mapFromEntities(entities: List<DetailEntity>): List<Detail> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntities(models: List<Detail>): List<DetailEntity> {
        return models.map { mapToEntity(it) }
    }

    override fun mapFromEntity(entity: DetailEntity): Detail {
        return when (entity.type) {
            DetailType.AUDIO -> AudioDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                convertDateTime(entity.dateTime)
            )
            DetailType.TEXT -> TextDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                convertDateTime(entity.dateTime)
            )
            DetailType.DRAWING -> DrawingDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                convertDateTime(entity.dateTime)
            )
            DetailType.PHOTO -> PhotoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                convertDateTime(entity.dateTime)
            )
            DetailType.EXTERNAL_VIDEO -> ExternalVideoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                convertDateTime(entity.dateTime)
            )
            DetailType.VIDEO -> YoutubeVideoDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                entity.videoId,
                convertDateTime(entity.dateTime)
            )
            DetailType.FILM -> FilmDetail(
                FileConverter().toFile(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid),
                convertDateTime(entity.dateTime)
            )
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }

    override fun mapToEntity(model: Detail): DetailEntity {
        return DetailEntity(
            FileConverter().toString(model.file),
            model.title,
            model.uuid.toString(),
            model.videoId,
            LocalDateTimeConverter().toString(model.dateTime),
            when (model) {
                is AudioDetail -> DetailType.AUDIO
                is TextDetail -> DetailType.TEXT
                is DrawingDetail -> DetailType.DRAWING
                is PhotoDetail -> DetailType.PHOTO
                is ExternalVideoDetail -> DetailType.EXTERNAL_VIDEO
                is YoutubeVideoDetail -> DetailType.VIDEO
                is FilmDetail -> DetailType.FILM
                else -> throw ClassCastException("Unknown Detail subclass encountered")
            }
        )
    }

    private fun convertDateTime(dateTime: String): LocalDateTime {
        return if (dateTime == "") LocalDateTime.now() else LocalDateTimeConverter().toDate(dateTime)
    }
}
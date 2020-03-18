package be.hogent.faith.storage.backpack

import be.hogent.faith.domain.models.detail.*
import java.io.File
import java.util.UUID

object DummyDetailMapper {
    fun mapFromEntities(entities: List<DummyDetail>): List<Detail> {
        return entities.map { mapFromEntity(it) }
    }

    fun mapToEntities(models: List<Detail>): List<DummyDetail> {
        return models.map { mapToEntity(it) }
    }

    fun mapFromEntity(entity: DummyDetail): Detail {
        return when (entity.type) {
            "AUDIO" -> AudioDetail(
                File(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            "TEXT" -> TextDetail(
                File(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            "DRAWING" -> DrawingDetail(
                File(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            "PHOTO" -> PhotoDetail(
                File(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            "VIDEO" -> VideoDetail(
                File(entity.file),
                entity.fileName,
                UUID.fromString(entity.uuid)
            )
            "EXTERNALVIDEO" -> ExternalVideoDetail(
                    File(entity.file),
                    entity.fileName,
                    UUID.fromString(entity.uuid)
            )
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }

    fun mapToEntity(model: Detail): DummyDetail {
        return DummyDetail(
            model.file.absolutePath, model.fileName, model.uuid.toString(), when (model) {
                is AudioDetail -> "AUDIO"
                is TextDetail -> "TEXT"
                is DrawingDetail -> "DRAWING"
                is PhotoDetail -> "PHOTO"
                is VideoDetail -> "VIDEO"
                is ExternalVideoDetail -> "EXTERNALVIDEO"
                else -> throw ClassCastException("Unknown Detail subclass encountered")
            }
        )
    }
}

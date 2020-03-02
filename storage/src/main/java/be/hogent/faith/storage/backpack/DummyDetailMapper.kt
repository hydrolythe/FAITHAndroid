package be.hogent.faith.storage

import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
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
                UUID.fromString(entity.uuid)
            )
            "TEXT" -> TextDetail(
                File(entity.file),
                UUID.fromString(entity.uuid)
            )
            "DRAWING" -> DrawingDetail(
                File(entity.file),
                UUID.fromString(entity.uuid)
            )
            "PHOTO" -> PhotoDetail(
                File(entity.file),
                UUID.fromString(entity.uuid)
            )
            else -> throw ClassCastException("Unknown DetailEntity subclass encountered")
        }
    }

    fun mapToEntity(model: Detail): DummyDetail {
        return DummyDetail(
                model.file.absolutePath, model.uuid.toString(), when (model) {
                is AudioDetail -> "AUDIO"
                is TextDetail -> "TEXT"
                is DrawingDetail -> "DRAWING"
                is PhotoDetail -> "PHOTO"
                else -> throw ClassCastException("Unknown Detail subclass encountered")
            }
        )
    }
}

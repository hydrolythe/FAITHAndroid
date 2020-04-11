package be.hogent.faith.database.event

import be.hogent.faith.database.Mapper
import be.hogent.faith.database.common.DetailMapper
import be.hogent.faith.service.encryption.EncryptedEvent
import java.io.File
import java.util.UUID

internal object EventMapper :
    Mapper<EncryptedEventEntity, EncryptedEvent> {

    override fun mapFromEntity(entity: EncryptedEventEntity): EncryptedEvent {
        return EncryptedEvent(
            dateTime = entity.dateTime,
            title = entity.title,
            emotionAvatar = entity.emotionAvatar?.let { File(it) },
            notes = entity.notes,
            uuid = UUID.fromString(entity.uuid),
            details = DetailMapper.mapFromEntities(
                entity.details
            ),
            encryptedDEK = entity.encryptedDEK,
            encryptedStreamingDEK = entity.encryptedStreamingDEK
        )
    }

    override fun mapToEntity(model: EncryptedEvent): EncryptedEventEntity {
        return EncryptedEventEntity(
            dateTime = model.dateTime,
            title = model.title,
            emotionAvatar = model.emotionAvatar?.path,
            notes = model.notes,
            uuid = model.uuid.toString(),
            details = DetailMapper.mapToEntities(
                model.details
            ),
            encryptedDEK = model.encryptedDEK,
            encryptedStreamingDEK = model.encryptedStreamingDEK
        )
    }

    override fun mapToEntities(models: List<EncryptedEvent>): List<EncryptedEventEntity> {
        return models.map(::mapToEntity)
    }

    override fun mapFromEntities(entities: List<EncryptedEventEntity>): List<EncryptedEvent> {
        return entities.map(::mapFromEntity)
    }
}

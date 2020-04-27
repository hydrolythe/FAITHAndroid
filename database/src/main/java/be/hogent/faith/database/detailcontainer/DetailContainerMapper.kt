package be.hogent.faith.database.detailcontainer

import be.hogent.faith.database.Mapper
import be.hogent.faith.service.encryption.EncryptedDetailsContainer

class DetailContainerMapper : Mapper<EncryptedDetailsContainerEntity, EncryptedDetailsContainer> {
    override fun mapFromEntity(entity: EncryptedDetailsContainerEntity): EncryptedDetailsContainer {
        with(entity) {
            return EncryptedDetailsContainer(
                encryptedDEK = encryptedDEK,
                encryptedStreamingDEK = encryptedStreamingDEK
            )
        }
    }

    override fun mapToEntity(model: EncryptedDetailsContainer): EncryptedDetailsContainerEntity {
        with(model) {
            return EncryptedDetailsContainerEntity(
                encryptedDEK = encryptedDEK,
                encryptedStreamingDEK = encryptedStreamingDEK
            )
        }
    }

    override fun mapFromEntities(entities: List<EncryptedDetailsContainerEntity>): List<EncryptedDetailsContainer> {
        return entities.map(::mapFromEntity)
    }

    override fun mapToEntities(models: List<EncryptedDetailsContainer>): List<EncryptedDetailsContainerEntity> {
        return models.map(::mapToEntity)
    }
}
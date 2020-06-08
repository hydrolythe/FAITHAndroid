package be.hogent.faith.database.detailcontainer

import be.hogent.faith.database.Mapper
import be.hogent.faith.service.encryption.ContainerType
import be.hogent.faith.service.encryption.EncryptedDetailsContainer

object DetailContainerMapper : Mapper<EncryptedDetailsContainerEntity, EncryptedDetailsContainer> {
    override fun mapFromEntity(entity: EncryptedDetailsContainerEntity): EncryptedDetailsContainer {
        with(entity) {
            return EncryptedDetailsContainer(
                containerType = stringToContainerType[type]
                    ?: error("Invalid container type found"),
                encryptedDEK = encryptedDEK,
                encryptedStreamingDEK = encryptedStreamingDEK
            )
        }
    }

    override fun mapToEntity(model: EncryptedDetailsContainer): EncryptedDetailsContainerEntity {
        with(model) {
            return EncryptedDetailsContainerEntity(
                type = containerTypeToString[model.containerType]
                    ?: error("Invalid container type found"),
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

    val containerTypeToString = mapOf(
        ContainerType.BACKPACK to "backpack",
        ContainerType.CINEMA to "cinema"
    )
    val stringToContainerType = mapOf(
        "backpack" to ContainerType.BACKPACK,
        "cinema" to ContainerType.CINEMA
    )
}
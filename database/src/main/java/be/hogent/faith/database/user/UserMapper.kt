package be.hogent.faith.database.user

import be.hogent.faith.database.Mapper
import be.hogent.faith.domain.models.User

object UserMapper : Mapper<UserEntity, User> {

    override fun mapFromEntity(entity: UserEntity): User {
        return User(
            uuid = entity.uuid,
            username = entity.username,
            avatarName = entity.avatarName
        )
    }

    override fun mapToEntity(model: User): UserEntity {
        return UserEntity(
            uuid = model.uuid,
            username = model.username,
            avatarName = model.avatarName.toString()
        )
    }

    override fun mapFromEntities(entities: List<UserEntity>): List<User> {
        return entities.map {
            mapFromEntity(
                it
            )
        }
    }

    override fun mapToEntities(models: List<User>): List<UserEntity> {
        return models.map {
            mapToEntity(
                it
            )
        }
    }
}
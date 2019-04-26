package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.User

object UserMapper : Mapper<UserEntity, User> {

    override fun mapFromEntity(entity: UserEntity): User {
        return User(entity.username, entity.avatar, entity.uuid)
    }

    override fun mapToEntity(model: User): UserEntity {
        return UserEntity(model.uuid, model.username, model.avatar)
    }
    override fun mapFromEntities(entities: List<UserEntity>): List<User> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntities(models: List<User>): List<UserEntity> {
        return models.map { mapToEntity(it) }
    }
}
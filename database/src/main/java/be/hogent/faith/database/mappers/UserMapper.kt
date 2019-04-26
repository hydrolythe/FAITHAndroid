package be.hogent.faith.database.mappers

import be.hogent.faith.database.models.UserEntity
import be.hogent.faith.domain.models.Avatar
import be.hogent.faith.domain.models.User

object UserMapper : Mapper<UserEntity, User> {

    override fun mapFromEntity(entity: UserEntity): User {
        return User(
            uuid = entity.uuid,
            username = entity.userName,
            avatar = Avatar(Integer.parseInt(entity.avatar))
        )
    }

    override fun mapToEntity(model: User): UserEntity {
        return UserEntity(
            uuid = model.uuid,
            userName = model.username,
            // TODO: avatarmapper gebruiken?
            avatar = model.avatar!!.imageUrl.toString()
        )
    }

    override fun mapFromEntities(entities: List<UserEntity>): List<User> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntities(models: List<User>): List<UserEntity> {
        return models.map { mapToEntity(it) }
    }
}
package be.hogent.faith.database.common

import be.hogent.faith.database.factory.EntityFactory
import be.hogent.faith.database.user.UserEntity
import be.hogent.faith.database.user.UserMapper
import be.hogent.faith.domain.models.User
import be.hogent.faith.util.factory.UserFactory
import org.junit.Assert.assertEquals
import org.junit.Test

class UserMapperTest {
    private val userMapper = UserMapper

    @Test
    fun `should map to User when UserEntity is given`() {
        val entity = EntityFactory.makeUserEntity()
        val model = userMapper.mapFromEntity(entity)
        assertEqualData(entity, model)
    }

    @Test
    fun `should map to UserEntity when User is given`() {
        val model = UserFactory.makeUser()
        val entity = userMapper.mapToEntity(model)
        assertEqualData(entity, model)
    }

    private fun assertEqualData(entity: UserEntity, model: User) {
        assertEquals(entity.uuid, model.uuid)
        assertEquals(entity.avatarName, model.avatarName)
        assertEquals(entity.username, model.username)
    }
}
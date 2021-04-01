package be.hogent.faith.faith.loginOrRegister.registerAvatar

import be.hogent.faith.faith.UserResult
import be.hogent.faith.faith.models.User

interface IRegisterAvatarRepository {
    suspend fun registerAvatar(user: User): UserResult
}
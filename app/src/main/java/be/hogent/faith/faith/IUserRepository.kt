package be.hogent.faith.faith

import be.hogent.faith.faith.models.User

interface IUserRepository {
    suspend fun login(idToken:String?):TokenResult?
    suspend fun getUser(): UserResult?
}
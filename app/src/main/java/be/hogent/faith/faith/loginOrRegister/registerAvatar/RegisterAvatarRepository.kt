package be.hogent.faith.faith.loginOrRegister.registerAvatar

import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.UserResult
import be.hogent.faith.faith.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterAvatarRepository:IRegisterAvatarRepository {
    override suspend fun registerAvatar(user: User): UserResult {
        return withContext(Dispatchers.IO){
            val task = RetrofitAdapter.registerAvatarApiService.register(user)
            try {
                val result = task.execute()
                UserResult(success=result.body())
            } catch(e:Exception){
                UserResult(exception=e)
            }
        }
    }
}
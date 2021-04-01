package be.hogent.faith.faith

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(val auth: FirebaseAuth) : IUserRepository {
    override suspend fun login(idToken:String?): TokenResult {
        return withContext(Dispatchers.IO) {
            val request = RetrofitAdapter.userApiService.login("Bearer $idToken")
            try {
                val execute = request.execute()
                TokenResult(success = execute.body())
            } catch (e: Exception) {
                TokenResult(exception = e)
            }
        }
    }

    override suspend fun getUser(): UserResult {
        return withContext(Dispatchers.IO) {
            val uid = auth.currentUser?.uid
            val request = RetrofitAdapter.userApiService.getUser(uid)
            try {
                val user = request.execute()
                val result = user.body()
                UserResult(success=result)
            } catch (e: Exception) {
                UserResult(exception=e)
            }
        }
    }
}
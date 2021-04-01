package be.hogent.faith.faith.cityScreen

import be.hogent.faith.faith.RetrofitAdapter
import be.hogent.faith.faith.VoidResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityScreenRepository(val firebaseAuth: FirebaseAuth):ICityScreenRepository {
    override suspend fun logout(): VoidResult {
        return withContext(Dispatchers.IO){
            val call = RetrofitAdapter.cityScreenApiService.logout(firebaseAuth.currentUser!!.uid)
            try {
                val result = call.execute()
                VoidResult(success=result.body())
            }catch (e:Exception){
                VoidResult(exception=e)
            }
        }
    }
}
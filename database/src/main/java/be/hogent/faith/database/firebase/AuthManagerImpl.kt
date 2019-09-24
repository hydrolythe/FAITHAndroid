package be.hogent.faith.database.firebase

import android.util.Log
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.repository.AuthManager
import be.hogent.faith.util.TAG
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import durdinapps.rxfirebase2.RxFirebaseAuth
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.UUID

class AuthManagerImpl : AuthManager {
    private var auth: FirebaseAuth;

    init {
        auth = FirebaseAuth.getInstance()
    }

    val CurrentUser:String?
            get ()= mapToUser(auth.currentUser)

    override fun register(email:String, password:String): Maybe<String> {
        try {
            return RxFirebaseAuth.createUserWithEmailAndPassword(auth, email, password)
                .map { mapToUser(it) }
        }
        catch (e:Exception) {
            Log.e(TAG, "fout ${e.message}")
            return Maybe.empty()
        }
    }

    override fun signIn(email:String, password:String):Maybe<String>{
        return RxFirebaseAuth.signInWithEmailAndPassword(auth, email, password)
            .map{mapToUser(it)}
    }

    override fun signOut():Completable {
        return try {
            auth.signOut()
            Completable.complete()
        } catch (e: Exception) {
            Completable.error(e)
        }
    }

    override fun isUnique(email:String): Single<Boolean> {
        return Single.just(true)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reset(){
        throw NotImplementedError()
    }

    private fun mapToUser (result: AuthResult) : String?{
            return result.user?.uid
    }

    private fun mapToUser (result: FirebaseUser?) : String?{
            return  result?.uid
    }
}
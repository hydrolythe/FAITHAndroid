package be.hogent.faith.database.authentication

import be.hogent.faith.service.repositories.SignInException
import be.hogent.faith.service.repositories.SignOutException
import be.hogent.faith.service.repositories.UserCollisionException
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import org.junit.Before
import org.junit.Test
import java.util.UUID

class AuthManagerImplTest {
    private val firebaseAuthManager = mockk<FirebaseAuthManager>(relaxed = true)
    private val authManagerImpl = AuthManager(firebaseAuthManager)
    private val email = "jan@faith.be"
    private val password = "azerty"
    private val uuid = UUID.randomUUID().toString()

    @Before
    fun setUp() {
    }

    @Test
    fun firebaseAuthManager_isUserNameUnique_WhenUnique_Completes() {
        every { firebaseAuthManager.isUsernameUnique(email) } returns Single.just(true)
        authManagerImpl.isUsernameUnique(email).test()
            .assertValue { it == true }
    }

    @Test
    fun firebaseAuthManager__isUserNameUnique_WhenNotUnique_Fails() {
        every { firebaseAuthManager.isUsernameUnique(email) } returns Single.just(false)
        authManagerImpl.isUsernameUnique(email).test()
            .assertValue { it == false }
    }

    @Test
    fun firebaseAuthManager_signIn_WhenEmailAndPasswordCorrect_Completes() {
        every { firebaseAuthManager.signIn(email, password) } returns Maybe.just(uuid)
        authManagerImpl.signIn(email, password).test()
            .assertValue { it == uuid }
    }

    @Test
    fun firebaseAuthManager_signIn_WhenEmailAndPasswordError_Fails() {
        every { firebaseAuthManager.signIn(email, password) } returns Maybe.error(
            SignInException(mockk())
        )
        authManagerImpl.signIn(email, password).test()
            .assertNoValues()
            .assertError(SignInException::class.java)
    }

    @Test
    fun firebaseAuthManager_register_WhenEmailAndPasswordCorrect_Completes() {
        every { firebaseAuthManager.register(email, password) } returns Maybe.just(uuid)
        authManagerImpl.register(email, password).test()
            .assertValue { it == uuid }
    }

    @Test
    fun firebaseAuthManager_register_WhenEmailAndPasswordError_Fails() {
        every { firebaseAuthManager.register(email, password) } returns Maybe.error(
            UserCollisionException(mockk())
        )
        authManagerImpl.register(email, password).test()
            .assertNoValues()
            .assertError(UserCollisionException::class.java)
    }

    @Test
    fun firebaseAuthManager_signOut_Completes() {
        every { firebaseAuthManager.signOut() } returns Completable.complete()
        authManagerImpl.signOut().test().assertComplete().assertNoErrors()
    }

    @Test
    fun firebaseAuthManager_signOut_Fails() {
        every { firebaseAuthManager.signOut() } returns Completable.error(
            SignOutException(mockk())
        )
        authManagerImpl.signOut().test().assertNotComplete()
            .assertError(SignOutException::class.java)
    }
}
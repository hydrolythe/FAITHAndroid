package be.hogent.faith.faith.loginOrRegister

import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.loginOrRegister.registerAvatar.Avatar
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.CreateUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.observers.DisposableSingleObserver

class RegisterUserViewModel(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _userRegisteredSuccessFully = SingleLiveEvent<User>()
    val userRegisteredSuccessFully: LiveData<User>
        get() = _userRegisteredSuccessFully

    fun registerUser(uuid: String, userName: String, avatar: Avatar) {
        val params = CreateUserUseCase.Params(
            userName,
            avatar.avatarName,
            uuid
        )
        createUserUseCase.execute(params, CreateUserUseCaseHandler())
    }

    private inner class CreateUserUseCaseHandler : DisposableSingleObserver<User>() {
        override fun onSuccess(newUser: User) {
            _userRegisteredSuccessFully.postValue(newUser)
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, "Registering user failed ${e.message}")
        }
    }

    override fun onCleared() {
        createUserUseCase.dispose()
        super.onCleared()
    }
}
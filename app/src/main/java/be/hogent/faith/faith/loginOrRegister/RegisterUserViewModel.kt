package be.hogent.faith.faith.loginOrRegister

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.CreateUserUseCase
import io.reactivex.observers.DisposableSingleObserver

class RegisterUserViewModel(
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _userSavedSuccessFully = SingleLiveEvent<User>()
    val userSavedSuccessFully: LiveData<User>
        get() = _userSavedSuccessFully

    fun registerUser(userName: String, password: String, avatarName: String) {
        val params = CreateUserUseCase.Params(
            userName,
            password,
            avatarName
        )
        createUserUseCase.execute(params, CreateUserUseCaseHandler())
    }

    private inner class CreateUserUseCaseHandler : DisposableSingleObserver<User>() {
        override fun onSuccess(newUser: User) {
            _userSavedSuccessFully.postValue(newUser)
        }

        override fun onError(e: Throwable) {
            _errorMessage.postValue(R.string.register_error_create_user)
        }
    }

    override fun onCleared() {
        createUserUseCase.dispose()
        super.onCleared()
    }
}
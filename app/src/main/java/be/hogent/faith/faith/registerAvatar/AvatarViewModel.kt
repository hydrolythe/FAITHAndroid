package be.hogent.faith.faith.registerAvatar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.CreateUserUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver

/**
 * ViewModel for the Avatar selection screen
 */
class AvatarViewModel(
    private val avatarProvider: AvatarProvider,
    private val createUserUseCase: CreateUserUseCase
) :
    ViewModel() {

    private var _avatars = MutableLiveData<List<Avatar>>()
    val avatars: LiveData<List<Avatar>>
        get() = _avatars

    private var _selectedItem = MutableLiveData<Long>()

    private val disposables = CompositeDisposable()

    private val _nextButtonClicked = SingleLiveEvent<Unit>()
    val nextButtonClicked: LiveData<Unit>
        get() = _nextButtonClicked

    /**
     * Updates with the new User when it was saved successfully.
     */
    private val _userSavedSuccessFully = SingleLiveEvent<User>()
    val userSavedSuccessFully: LiveData<User>
        get() = _userSavedSuccessFully

    private val _inputErrorMessageID = MutableLiveData<Int>()
    val inputErrorMessageID: LiveData<Int>
        get() = _inputErrorMessageID

    /**
     * Will be updated with the latest error message when an error occurs when saving
     */
    private val _userSaveFailed = MutableLiveData<String>()
    val userSaveFailed: LiveData<String>
        get() = _userSaveFailed

    var userName = MutableLiveData<String>()

    init {
        // Set initially to -1 = no selection has been provided.
        _selectedItem.value = -1

        fetchAvatarImages()
    }

    /**
     * The item selected in the recyclerView as the avatarName.
     */
    val selectedItem: LiveData<Long>
        get() = _selectedItem

    /**
     * Returns true if an Avatar has been selected, false if not.
     */
    fun avatarWasSelected(): Boolean {
        return _selectedItem.value!!.toInt() != -1
    }

    /**
     * Sets the selected item (the selected avatars). In this case this is
     * still the position in the recyclerView.
     */
    fun setSelectedItem(selectedItem: Long) {
        _selectedItem.postValue(selectedItem)
    }

    fun nextButtonPressed() {
        if (avatarWasSelected() && userName.value != null) {
            val params = CreateUserUseCase.Params(
                userName.value!!,
                _avatars.value!![_selectedItem.value!!.toInt()].avatarName
            )
            createUserUseCase.execute(CreateUserHandler(), params)
        } else {
            _inputErrorMessageID.postValue(R.string.txt_error_userNameOrAvatarNotSet)
            return
        }
        _nextButtonClicked.call()
    }

    private fun fetchAvatarImages() {
        _avatars.postValue(avatarProvider.getAvatars())
    }

    private inner class CreateUserHandler : DisposableSingleObserver<User>() {
        override fun onSuccess(newUser: User) {
            _userSavedSuccessFully.postValue(newUser)
        }

        override fun onError(e: Throwable) {
            _userSaveFailed.postValue(e.localizedMessage)
        }
    }

    override fun onCleared() {
        createUserUseCase.dispose()
        super.onCleared()
    }
}
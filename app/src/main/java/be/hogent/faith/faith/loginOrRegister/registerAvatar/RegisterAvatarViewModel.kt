package be.hogent.faith.faith.loginOrRegister.registerAvatar

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.faith.state.Resource
import be.hogent.faith.faith.state.ResourceState
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.user.InitialiseUserUseCase
import io.reactivex.rxjava3.observers.DisposableCompletableObserver

/**
 * Represents the [ViewModel] for the user during the registering process - Part 2
 * Namely the part where the user selects his/her avatar
 * If the avatar is available, the user will be registered
 */
class RegisterAvatarViewModel(
    private val avatarProvider: AvatarProvider,
    private val initialiseUserUseCase: InitialiseUserUseCase
) : ViewModel() {

    private var _avatars = MutableLiveData<List<Avatar>>()
    val avatars: LiveData<List<Avatar>>
        get() = _avatars

    val selectedAvatarPosition: LiveData<Int>
        get() = _selectedAvatarPosition
    private var _selectedAvatarPosition = MutableLiveData<Int>()

    val selectedSkinColor: LiveData<SkinColor>
        get() = _selectedSkinColor
    private var _selectedSkinColor = MutableLiveData<SkinColor>()

    private val _finishRegistrationClicked = SingleLiveEvent<Unit>() // SingleLiveEvent<User>()
    val finishRegistrationClicked: LiveData<Unit> // LiveData<User>
        get() = _finishRegistrationClicked

    private val _errorMessage = MutableLiveData<@IdRes Int>()
    val errorMessage: LiveData<Int>
        get() = _errorMessage

    private val _userRegisteredState = MutableLiveData<Resource<Unit>>()
    val userRegisteredState: LiveData<Resource<Unit>>
        get() = _userRegisteredState

    init {
        _selectedSkinColor.value = SkinColor.blank
        fetchAvatarImages()
        // Set initially to -1 = no selection has been provided.
        _selectedAvatarPosition.postValue(-1)
    }

    /**
     * Will always hold the [Avatar] corresponding with the [_selectedAvatarPosition].
     * Exposing a LiveData would be nicer (using a map on [_selectedAvatarPosition] but that only updates
     * when someone is actually observing this Livedata. As we only need it once no observing is needed.
     */
    val selectedAvatar: Avatar?
        get() {
            return if (avatarWasSelected()) {
                _avatars.value!![selectedAvatarPosition.value!!]
            } else {
                null
            }
        }

    /**
     * Returns true if an Avatar has been selected, false if not.
     */
    fun avatarWasSelected(): Boolean {
        with(selectedAvatarPosition.value) {
            return (this != null) && (this != -1)
        }
    }

    fun onFinishRegistrationClicked() {
        if (avatarWasSelected()) {
            _finishRegistrationClicked.call()
        } else {
            _errorMessage.postValue(R.string.register_avatar_mustSelect)
        }
    }

    /**
     * Sets the selected item (the selected avatars). In this case this is
     * still the position in the recyclerView.
     */
    fun setSelectedAvatar(position: Int) {
        _selectedAvatarPosition.postValue(position)
    }

    fun setSelectedSkinColor(skinColor: SkinColor) {
        _selectedSkinColor.value = skinColor
        fetchAvatarImages()
    }

    private fun fetchAvatarImages() {
        _avatars.value = (avatarProvider.getAvatars(selectedSkinColor.value!!))
    }

    fun initialiseUser(user: User) {
        _userRegisteredState.postValue(Resource(ResourceState.LOADING, Unit, null))
        user.avatarName = selectedAvatar!!.avatarName
        val params = InitialiseUserUseCase.Params(user)
        initialiseUserUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _userRegisteredState.postValue(Resource(ResourceState.SUCCESS, Unit, null))
            }

            override fun onError(e: Throwable) {
                _userRegisteredState.postValue(
                    Resource(
                        ResourceState.ERROR,
                        Unit,
                        R.string.register_error_create_user
                    )
                )
            }
        })
    }
}
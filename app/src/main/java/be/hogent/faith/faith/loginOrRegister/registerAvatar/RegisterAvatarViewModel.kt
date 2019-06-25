package be.hogent.faith.faith.loginOrRegister.registerAvatar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

/**
 * ViewModel for the Avatar selection screen
 */
class RegisterAvatarViewModel(
    private val avatarProvider: AvatarProvider
) : ViewModel() {

    private var _avatars = MutableLiveData<List<Avatar>>()
    val avatars: LiveData<List<Avatar>>
        get() = _avatars

    private var _selectedItem = MutableLiveData<Int>()

    private val _nextButtonClicked = SingleLiveEvent<Unit>()

    val nextButtonClicked: LiveData<Unit>
        get() = _nextButtonClicked

    /**
     * Will always hold the [Avatar] corresponding with the [_selectedItem].
     */
    val selectedAvatar: LiveData<Avatar> = Transformations.map(_selectedItem) { item -> _avatars.value!![item.toInt()] }

    init {
        fetchAvatarImages()
        // Set initially to -1 = no selection has been provided.
        _selectedItem.postValue(-1)
    }

    /**
     * The item selected in the recyclerView as the avatarName.
     */
    val selectedItem: LiveData<Int>
        get() = _selectedItem


    private fun fetchAvatarImages() {
        _avatars.value = (avatarProvider.getAvatars())
    }

}
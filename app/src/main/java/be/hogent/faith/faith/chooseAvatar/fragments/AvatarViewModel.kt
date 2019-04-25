package be.hogent.faith.faith.chooseAvatar.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.faith.util.AvatarProvider
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.CreateUserUseCase
import io.reactivex.disposables.CompositeDisposable

/**
 * ViewModel for the Avatar selection screen
 */
class AvatarViewModel(private val avatarRepository: AvatarProvider, private val createUserUseCase: CreateUserUseCase) : ViewModel() {

    /**
     * Private mutable live data object which keeps track of the AvatarItems.
     */
    private var _avatarItems = MutableLiveData<List<Avatar>>()

    private var _selectedItem = MutableLiveData<Long>()

    private val disposables = CompositeDisposable()

    private val _nextButtonClicked = SingleLiveEvent<Unit>()
    val nextButtonClicked: LiveData<Unit>
        get() = _nextButtonClicked

    private val _userSavedSuccessFully = SingleLiveEvent<Unit>()
    val userSavedSuccessFully: LiveData<Unit>
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

    /**
     * User name of the user
     */
    var userName = MutableLiveData<String>()

    init {
        // Set initially to -1 = no selection has been provided.
        _selectedItem.value = -1
        fetchItems()
    }

    /**
     * Getter for the avatarItems, but only returns a [LiveData<AvatarItem>] type.
     */
    val avatarItems: LiveData<List<Avatar>>
        get() = _avatarItems

    /**
     * The item selected in the recyclerView as the avatar.
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
            val params = CreateUserUseCase.Params(userName.value!!, _avatarItems.value?.get(_selectedItem.value!!.toInt())!!.imageName)
            val disposable = createUserUseCase.execute(params).subscribe({
                _userSavedSuccessFully.call()
            }, {
                _userSaveFailed.postValue(it.localizedMessage)
            })
            disposables.add(disposable)
        } else {
            _inputErrorMessageID.postValue(R.string.avatarNotSet)
            return
        }
        _nextButtonClicked.call()
    }

    /**
     * TODO: Needs to be adapted to the way Avatars will be provided (Network, DB, ...)
     */
    private fun fetchItems() {
        _avatarItems.postValue(avatarRepository.getAvatars())
    }
}
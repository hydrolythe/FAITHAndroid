package be.hogent.faith.faith.chooseAvatar.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.AvatarItem

/**
 * Abstract ViewModel class to represent the [AvatarItem] elements.
 */
abstract class AvatarItemViewModel : ViewModel() {

    /**
     * Private mutable live data object which keeps track of the AvatarItems.
     */
    protected var _avatarItems = MutableLiveData<List<AvatarItem>>()

    /**
     * Getter for the avatarItems, but only returns a [LiveData<AvatarItem>] type.
     */
    val avatarItems: LiveData<List<AvatarItem>>
    get() = _avatarItems

    init {
        fetchItems()
    }

    /**
     * Loads the items from whereever they should be loaded.
     * At this moment, from the Res folder.
     */
    abstract fun fetchItems()
}
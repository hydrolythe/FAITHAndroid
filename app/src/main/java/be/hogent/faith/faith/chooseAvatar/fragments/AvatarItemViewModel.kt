package be.hogent.faith.faith.chooseAvatar.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.AvatarItem

/**
 * Abstract ViewModel class to represent the [AvatarItem] elements.
 */
abstract class AvatarItemViewModel : ViewModel() {

    var avatarItems = MutableLiveData<List<AvatarItem>>()

    init {
        fetchItems()
    }

    /**
     * Loads the items from whereever they should be loaded.
     * At this moment, from the Res folder.
     */
    abstract fun fetchItems()
}
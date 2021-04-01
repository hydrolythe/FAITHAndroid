package be.hogent.faith.faith.backpack

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.models.Backpack
import com.google.firebase.auth.FirebaseAuth

class BackpackViewModel(
    backpack: Backpack,
    backpackRepository: BackpackRepository,
    context:Context
) : DetailsContainerViewModel<Backpack>(
    backpack,
    backpackRepository,
    context
) {

    private val _viewButtons = MutableLiveData<Boolean>()
    val viewButtons: LiveData<Boolean> = _viewButtons

    fun viewButtons(viewButtons: Boolean) {
        _viewButtons.postValue(viewButtons)
    }
}

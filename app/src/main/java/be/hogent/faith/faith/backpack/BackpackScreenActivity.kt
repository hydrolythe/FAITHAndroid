package be.hogent.faith.faith.backpack

import android.os.Bundle
import be.hogent.faith.R
import be.hogent.faith.faith.models.detail.Detail
import be.hogent.faith.faith.detailscontainer.DetailsContainerActivity
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.models.Backpack
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class BackpackScreenActivity : DetailsContainerActivity<Backpack>() {
    override val activityResourceId: Int = R.layout.activity_backpackscreen
    override val fragmentContainerResourceId: Int = R.id.backpack_fragment_container

    private lateinit var backpackViewModel: BackpackViewModel

    override val detailsContainerViewModel: DetailsContainerViewModel<Backpack>
        get() = backpackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backpackViewModel = getViewModel { parametersOf(userViewModel.user.value!!.backpack,applicationContext) }
    }

    override fun createNewFragmentInstance() = BackpackScreenFragment.newInstance()

    override fun backToEvent() {
        super.backToEvent()
        backpackViewModel.viewButtons(true)
    }

    override fun onDetailFinished(detail: Detail) {
        super.onDetailFinished(detail)
        backpackViewModel.viewButtons(true)
    }

    override fun setLayoutListenersOnNewDetailOpened() {
        super.setLayoutListenersOnNewDetailOpened()
        backpackViewModel.viewButtons(false)
    }

    override fun openDetailScreenFor(detail: Detail) {
        detailsContainerViewModel.setOpenDetailType(OpenDetailMode.VIEW)
        detailsContainerViewModel.setCurrentFileAndLoadCorrespondingFile(detail)
        backpackViewModel.viewButtons(false)
    }
}
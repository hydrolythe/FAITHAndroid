package be.hogent.faith.faith.treasureChest

import android.os.Bundle
import be.hogent.faith.R
import be.hogent.faith.domain.models.TreasureChest
import be.hogent.faith.faith.detailscontainer.DetailsContainerActivity
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class TreasureChestActivity : DetailsContainerActivity<TreasureChest>() {

    override val activityResourceId: Int = R.layout.activity_treasurechest
    override val fragmentContainerResourceId: Int = R.id.treasurechest_fragment_container
    override val detailsContainerViewModel: DetailsContainerViewModel<TreasureChest>
        get() = treasureChestViewModel

    private lateinit var treasureChestViewModel: TreasureChestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        treasureChestViewModel =
            getViewModel { parametersOf(userViewModel.user.value!!.treasureChest) }
    }

    override fun createNewFragmentInstance() = TreasureChestFragment.newInstance()
}
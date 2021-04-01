package be.hogent.faith.faith.treasureChest

import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentTreasurechestBinding
import be.hogent.faith.faith.models.TreasureChest
import be.hogent.faith.faith.detailscontainer.DetailsContainerFragment
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import kotlinx.android.synthetic.main.fragment_treasurechest.btn_treasurechest_add
import kotlinx.android.synthetic.main.fragment_treasurechest.btn_treasurechest_delete
import kotlinx.android.synthetic.main.fragment_treasurechest.rv_treasurechest
import org.koin.android.viewmodel.ext.android.sharedViewModel

class TreasureChestFragment : DetailsContainerFragment<TreasureChest>() {

    override val addButton: ImageButton
        get() = btn_treasurechest_add
    override val deleteButton: ImageButton
        get() = btn_treasurechest_delete
    override val detailsRecyclerView: RecyclerView
        get() = rv_treasurechest

    override val layoutResourceID: Int = R.layout.fragment_treasurechest
    override val menuResourceID: Int = R.menu.menu_treasurechest

    private val treasureChestViewModel: TreasureChestViewModel by sharedViewModel()

    override val detailsContainerViewModel: DetailsContainerViewModel<TreasureChest>
        get() = treasureChestViewModel

    private val treasureChestBinding: FragmentTreasurechestBinding
        get() = binding as FragmentTreasurechestBinding

    companion object {
        fun newInstance(): TreasureChestFragment {
            return TreasureChestFragment()
        }
    }

    override fun setViewModel() {
        treasureChestBinding.treasureChestViewModel = treasureChestViewModel
    }
}
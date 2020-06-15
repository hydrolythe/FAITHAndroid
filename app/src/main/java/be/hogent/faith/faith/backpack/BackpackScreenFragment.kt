package be.hogent.faith.faith.backpack

import android.widget.ImageButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentBackpackBinding
import be.hogent.faith.domain.models.Backpack
import be.hogent.faith.faith.detailscontainer.DetailsContainerFragment
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import kotlinx.android.synthetic.main.fragment_backpack.btn_backpack_add
import kotlinx.android.synthetic.main.fragment_backpack.btn_backpack_delete
import kotlinx.android.synthetic.main.fragment_backpack.recyclerview_backpack
import org.koin.android.viewmodel.ext.android.sharedViewModel

class BackpackScreenFragment : DetailsContainerFragment<Backpack>() {

    override val addButton: ImageButton
        get() = btn_backpack_add
    override val deleteButton: ImageButton
        get() = btn_backpack_delete
    override val detailsRecyclerView: RecyclerView
        get() = recyclerview_backpack

    override val layoutResourceID: Int = R.layout.fragment_backpack
    override val menuResourceID: Int = R.menu.menu_backpack

    private val backpackViewModel: BackpackViewModel by sharedViewModel()

    override val detailsContainerViewModel: DetailsContainerViewModel<Backpack>
        get() = backpackViewModel

    private val backpackBinding: FragmentBackpackBinding
        get() = binding as FragmentBackpackBinding

    override fun setViewModel() {
        backpackBinding.backpackViewModel = backpackViewModel
    }

    override fun onStart() {
        super.onStart()
        backpackViewModel.viewButtons(true)
        startDetailFilterListeners()
    }

    private fun startDetailFilterListeners() {
        backpackViewModel.textFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setFilterStateDrawable(
                enabled,
                backpackBinding.backpackMenuFilter.filterknopTeksten,
                R.drawable.ic_filterknop_teksten,
                R.drawable.ic_filterknop_teksten_selected
            )
        })
        backpackViewModel.audioFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setFilterStateDrawable(
                enabled,
                backpackBinding.backpackMenuFilter.filterknopAudio,
                R.drawable.ic_filterknop_audio,
                R.drawable.ic_filterknop_audio_selected
            )
        })
        backpackViewModel.photoFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setFilterStateDrawable(
                enabled,
                backpackBinding.backpackMenuFilter.filterknopFoto,
                R.drawable.ic_filterknop_foto,
                R.drawable.ic_filterknop_foto_selected
            )
        })
        backpackViewModel.drawingFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setFilterStateDrawable(
                enabled,
                backpackBinding.backpackMenuFilter.filterknopTekeningen,
                R.drawable.ic_filterknop_tekeningen,
                R.drawable.ic_filterknop_tekeningen_selected
            )
        })
        // TODO
        backpackViewModel.videoFilterEnabled.observe(viewLifecycleOwner, Observer { enabled ->
            setFilterStateDrawable(
                enabled,
                backpackBinding.backpackMenuFilter.filterknopFilm,
                R.drawable.filterknop_film,
                R.drawable.circle_green // Placeholder, juiste drawable ontbreekt
            )
        })
        // TODO
        backpackViewModel.externalVideoFilterEnabled.observe(
            viewLifecycleOwner,
            Observer { enabled ->
                setFilterStateDrawable(
                    enabled,
                    backpackBinding.backpackMenuFilter.filterknopExternBestand,
                    R.drawable.filterknop_extern_bestand,
                    R.drawable.circle_green // Placeholder, juiste drawable ontbreekt
                )
            })
    }

    private fun setFilterStateDrawable(
        filterEnabled: Boolean,
        filterButton: ImageButton,
        disabledImageResId: Int,
        enabledFilterResId: Int
    ) {
        filterButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this.requireContext(),
                if (filterEnabled) enabledFilterResId else disabledImageResId
            )
        )
    }

    interface BackpackDetailsNavigationListener : DetailsContainerNavigationListener

    companion object {
        fun newInstance(): BackpackScreenFragment {
            return BackpackScreenFragment()
        }
    }
}
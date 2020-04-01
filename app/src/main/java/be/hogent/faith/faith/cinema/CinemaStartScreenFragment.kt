package be.hogent.faith.faith.cinema

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentCinemaStartBinding
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailThumbnailsAdapter
import timber.log.Timber

class CinemaStartScreenFragment : Fragment() {


    private var navigation: CinemaNavigationListener? = null
    private var detailThumbnailsAdapter: DetailThumbnailsAdapter? = null
    private lateinit var addDetailMenu: PopupMenu
    private lateinit var binding: FragmentCinemaStartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cinema_start, container, false)
        binding.lifecycleOwner = this


        return binding.root

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CinemaNavigationListener) {
            navigation = context
        }
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        updateUI()
    }
    private fun updateUI() {
        detailThumbnailsAdapter = DetailThumbnailsAdapter(
                emptyList(),
                requireNotNull(activity) as CinemaActivity
        )
        binding.rvCinema.layoutManager = GridLayoutManager(activity, 6)
        binding.rvCinema.adapter = detailThumbnailsAdapter
    }
    private fun startListeners() {

        binding.btnDetails.setOnClickListener {
            binding.btnCinemaAdd.visibility = View.VISIBLE
        }
        binding.btnCinemaAdd.setOnClickListener {
            addDetailMenu = PopupMenu(binding.btnCinemaAdd.context, binding.btnCinemaAdd, Gravity.TOP, 0, R.style.PopupMenu_AddDetail)
            addDetailMenu.gravity = Gravity.TOP
            addDetailMenu.menuInflater.inflate(R.menu.menu_cinema, addDetailMenu.menu)

            try {
                val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopup.isAccessible = true
                val mPopup = fieldMPopup.get(addDetailMenu)
                mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
            } catch (e: Exception) {
                Timber.e("Error showing icons")
            }
            addDetailMenu.show()
        }

        binding.btnFilms.setOnClickListener{
            binding.btnCinemaAdd.visibility = View.GONE
        }
        binding.btnCinemaCancel.setOnClickListener{
            navigation!!.closeCinema()
        }
    }

    companion object {
        fun newInstance(): CinemaStartScreenFragment {
            return CinemaStartScreenFragment()
        }
    }

    interface CinemaNavigationListener {
        fun startPhotoDetailFragment()
        fun startDrawingDetailFragment()
        fun closeCinema()
    }

}

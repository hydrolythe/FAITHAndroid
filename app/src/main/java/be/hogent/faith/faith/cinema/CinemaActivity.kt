package be.hogent.faith.faith.cinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.backpackScreen.BackpackDetailFragment
import be.hogent.faith.faith.backpackScreen.DeleteDetailDialog
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CinemaActivity : AppCompatActivity(), CinemaStartScreenFragment.CinemaNavigationListener,
    DetailViewHolder.ExistingDetailNavigationListener,
    DeleteDetailDialog.DeleteDetailDialogListener,
    CinemaCreateVideoFragment.CinemaCreateVideoFragmentNavigationListener {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val cinemaOverviewViewModel: CinemaOverviewViewModel by viewModel {
        parametersOf(
            userViewModel.user.value!!.cinema
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema)

        if (savedInstanceState == null) {
            val fragment = CinemaStartScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.cinema_fragment_container, fragment)
                .commit()
        }

        cinemaOverviewViewModel.goToCityScreen.observe(this, Observer {
            //     closeCinema()
        })

        cinemaOverviewViewModel.goToDetail.observe(this, Observer {
            replaceFragment(
                BackpackDetailFragment.newInstance(it),
                R.id.backpack_fragment_container
            )
        })
    }

    //   override fun closeCinema() {
    //       closeCinemaSpecificScopes()
    //       finish()
    //   }

    private fun closeCinemaSpecificScopes() {
        // Close the drawing scope so unfinished drawings aren't shown when capturing
        // a new event.
        runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
            it.close()
        }
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(TakePhotoFragment.newInstance(), R.id.cinema_fragment_container)
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(DrawingDetailFragment.newInstance(), R.id.cinema_fragment_container)
    }

    override fun startExternalFileDetailFragment() {
        TODO("Not yet implemented")
    }

    override fun startCreateVideoFragment() {
        replaceFragment(CinemaCreateVideoFragment.newInstance(), R.id.cinema_fragment_container)
    }

    override fun closeCinema() {
        finish()
    }

    override fun startViewVideoFragment() {
        TODO("Not yet implemented")
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }

    override fun openDetailScreenFor(detail: Detail) {
        TODO("Not yet implemented")
    }

    override fun deleteDetail(detail: Detail) {
        val dialog = DeleteDetailDialog.newInstance(detail)
        dialog.show(supportFragmentManager, "DeleteDetailDialog")
    }

    override fun onDetailDeleteClick(dialog: DialogFragment, detail: Detail) {
        TODO("Not yet implemented")
    }

    override fun onDetailCancelClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}

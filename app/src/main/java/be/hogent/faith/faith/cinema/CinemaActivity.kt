package be.hogent.faith.faith.cinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.backpackScreen.DeleteDetailDialog
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder

class CinemaActivity : AppCompatActivity(), CinemaStartScreenFragment.CinemaNavigationListener, DetailViewHolder.ExistingDetailNavigationListener,
        DeleteDetailDialog.DeleteDetailDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema)

        if (savedInstanceState == null) {
            val fragment = CinemaStartScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.cinema_fragment_container, fragment)
                    .commit()
        }
    }

    override fun startPhotoDetailFragment() {
        TODO("Not yet implemented")
    }

    override fun startDrawingDetailFragment() {
        TODO("Not yet implemented")
    }

    override fun startExternalFileDetailFragment() {
        TODO("Not yet implemented")
    }

    override fun closeCinema() {
        finish()
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

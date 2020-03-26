package be.hogent.faith.faith.backpackScreen

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.ExternalVideoDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.faith.backpackScreen.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.getViewModel

class BackpackScreenActivity : AppCompatActivity(), BackpackScreenFragment.BackpackDetailsNavigationListener,
        RecordAudioFragment.AudioScreenNavigation,
        DrawFragment.DrawingScreenNavigation,
        DetailFinishedListener,
        TextDetailFragment.TextScreenNavigation,
        TakePhotoFragment.PhotoScreenNavigation,
        DetailViewHolder.ExistingDetailNavigationListener,
        AddExternalFileFragment.ExternalFileScreenNavigation,
        DeleteDetailDialog.DeleteDetailDialogListener {

    private lateinit var backpackViewModel: BackpackViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backpackscreen)

        backpackViewModel = getViewModel()

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = BackpackScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit()
        }

        backpackViewModel.goToCityScreen.observe(this, Observer {
            closeBackpack()
        })

        backpackViewModel.goToDetail.observe(this, Observer {
            replaceFragment(
                BackpackDetailFragment.newInstance(it),
                R.id.fragment
            )
        })
    }

    // BackToBackpack overrides method already defined in details
    override fun backToEvent() {
        supportFragmentManager.popBackStack()
        backpackViewModel.viewButtons(true)
        backpackViewModel.setDetailScreenOpen(false)

    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is TextDetail -> save(detail)
            is PhotoDetail -> save(detail)
            is AudioDetail -> save(detail)
            is ExternalVideoDetail -> save(detail)
        }
        backpackViewModel.viewButtons(true)
        backpackViewModel.setDetailScreenOpen(false)

    }

    override fun startPhotoDetailFragment() {
        replaceFragment(BackpackDetailFragment.PhotoFragment.newInstance(), R.id.fragment)
        startFragmentInitialisers()
    }

    override fun startAudioDetailFragment() {
        replaceFragment(BackpackDetailFragment.AudioFragment.newInstance(), R.id.fragment)
        startFragmentInitialisers()
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(BackpackDetailFragment.DrawingFragment.newInstance(), R.id.fragment)
        startFragmentInitialisers()
    }

    override fun startTextDetailFragment() {
        replaceFragment(BackpackDetailFragment.TextFragment.newInstance(), R.id.fragment)
        startFragmentInitialisers()
    }

    private fun startFragmentInitialisers() {
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
        backpackViewModel.setOpenDetailType(OpenDetailType.NEW)
    }

    override fun startVideoDetailFragment() {
        Toast.makeText(this, "Nog niet beschikbaar", Toast.LENGTH_SHORT).show()
    }

    override fun startExternalFileDetailFragment() {
        replaceFragment(BackpackDetailFragment.ExternalFileFragment.newInstance(), R.id.fragment)
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
    }

    override fun openDetailScreenFor(detail: Detail) {
        backpackViewModel.setOpenDetailType(OpenDetailType.EDIT)
        backpackViewModel.setCurrentFile(detail)
        replaceFragment(
                BackpackDetailFragment.newInstance(detail),
                R.id.fragment
        )
        backpackViewModel.viewButtons(false)
    }

    override fun closeBackpack() {
        closeBackpackSpecificScopes()
        finish()
    }

    private fun closeBackpackSpecificScopes() {
        // Close the drawing scope so unfinished drawings aren't shown when capturing
        // a new event.
        runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
            it.close()
        }
    }

    fun save(detail: Detail) {
        backpackViewModel.showSaveDialog(detail)
    }

    override fun deleteDetail(detail: Detail) {
        val dialog = DeleteDetailDialog.newInstance(detail)
        dialog.show(supportFragmentManager, "DeleteDetailDialog")
    }

    override fun onDetailDeleteClick(dialog: DialogFragment, detail: Detail) {
        backpackViewModel.deleteDetail(detail)
    }

    override fun onDetailCancelClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}
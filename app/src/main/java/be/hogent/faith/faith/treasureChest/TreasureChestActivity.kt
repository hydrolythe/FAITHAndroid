package be.hogent.faith.faith.treasureChest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.domain.models.detail.VideoDetail
import be.hogent.faith.domain.models.detail.YoutubeVideoDetail
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.backpack.DeleteDetailDialog
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.DetailType
import be.hogent.faith.faith.details.DetailsFactory
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.externalFile.AddExternalFileFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.details.youtubeVideo.create.YoutubeVideoDetailFragment
import be.hogent.faith.faith.details.youtubeVideo.view.ViewYoutubeVideoFragment
import be.hogent.faith.faith.detailscontainer.DetailsContainerFragment
import be.hogent.faith.faith.detailscontainer.OpenDetailMode
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TreasureChestActivity : AppCompatActivity(),
    DetailsContainerFragment.DetailsContainerNavigationListener,
    RecordAudioFragment.AudioScreenNavigation,
    DrawFragment.DrawingScreenNavigation,
    DetailFinishedListener,
    TextDetailFragment.TextScreenNavigation,
    TakePhotoFragment.PhotoScreenNavigation,
    DetailViewHolder.ExistingDetailNavigationListener,
    AddExternalFileFragment.ExternalFileScreenNavigation,
    DeleteDetailDialog.DeleteDetailDialogListener,
    YoutubeVideoDetailFragment.YoutubeVideoDetailScreenNavigation,
    ViewYoutubeVideoFragment.ViewYoutubeVideoNavigation {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private val treasureChestViewModel: TreasureChestViewModel by viewModel {
        parametersOf(
            userViewModel.user.value!!.treasureChest
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_treasurechest)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = TreasureChestFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.treasurechest_fragment_container, fragment)
                .commit()
        }

        treasureChestViewModel.goToCityScreen.observe(this, Observer {
            closeScreen()
        })

        treasureChestViewModel.goToDetail.observe(this, Observer {
            replaceFragment(
                DetailsFactory.editDetail(it),
                R.id.treasurechest_fragment_container
            )
        })
    }

    // BackToBackpack overrides method already defined in details
    override fun backToEvent() {
        supportFragmentManager.popBackStack()
    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is TextDetail -> save(detail)
            is PhotoDetail -> save(detail)
            is AudioDetail -> save(detail)
            is VideoDetail -> save(detail)
            is YoutubeVideoDetail -> save(detail)
        }
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.PHOTO),
            R.id.treasurechest_fragment_container
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startAudioDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.AUDIO),
            R.id.treasurechest_fragment_container
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.DRAWING),
            R.id.treasurechest_fragment_container
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startTextDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.TEXT),
            R.id.treasurechest_fragment_container
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startExternalFileDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.EXTERNALFILE),
            R.id.treasurechest_fragment_container
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startVideoDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.YOUTUBE),
            R.id.treasurechest_fragment_container
        )
        setLayoutListenersOnNewDetailOpened()
    }

    private fun setLayoutListenersOnNewDetailOpened() {
        treasureChestViewModel.setOpenDetailType(OpenDetailMode.NEW)
    }

    override fun openDetailScreenFor(detail: Detail) {
        treasureChestViewModel.setOpenDetailType(OpenDetailMode.EDIT)
        treasureChestViewModel.setCurrentFileAndLoadCorrespondingFile(detail)
    }

    override fun closeScreen() {
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
        treasureChestViewModel.saveCurrentDetail(userViewModel.user.value!!, detail)
    }

    override fun deleteDetail(detail: Detail) {
        val dialog = DeleteDetailDialog.newInstance(detail)
        dialog.show(supportFragmentManager, "DeleteDetailDialog")
    }

    override fun onDetailDeleteClick(dialog: DialogFragment, detail: Detail) {
        treasureChestViewModel.deleteDetail(detail)
    }

    override fun onDetailCancelClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}
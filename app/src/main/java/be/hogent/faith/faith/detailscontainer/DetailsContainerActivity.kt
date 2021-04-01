package be.hogent.faith.faith.detailscontainer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import be.hogent.faith.faith.models.DetailsContainer
import be.hogent.faith.faith.models.detail.Detail
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
import be.hogent.faith.faith.details.video.view.ViewVideoFragment
import be.hogent.faith.faith.details.youtubeVideo.create.YoutubeVideoDetailFragment
import be.hogent.faith.faith.details.youtubeVideo.view.ViewYoutubeVideoFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.util.replaceFragment
import org.koin.android.ext.android.getKoin

abstract class DetailsContainerActivity<T : DetailsContainer> : AppCompatActivity(),
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
    ViewYoutubeVideoFragment.ViewYoutubeVideoNavigation,
    ViewVideoFragment.ViewExternalVideoNavigation {

    protected val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    abstract val detailsContainerViewModel: DetailsContainerViewModel<T>

    abstract val activityResourceId: Int
    abstract val fragmentContainerResourceId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityResourceId)

        if (savedInstanceState == null) {
            val fragment = createNewFragmentInstance()
            supportFragmentManager.beginTransaction()
                .add(fragmentContainerResourceId, fragment)
                .commit()
        }
    }

    abstract fun createNewFragmentInstance(): DetailsContainerFragment<T>

    override fun onStart() {
        super.onStart()
        startListeners()
    }

    private fun startListeners() {
        detailsContainerViewModel.goToCityScreen.observe(this, Observer {
            closeScreen()
        })

        detailsContainerViewModel.goToDetail.observe(this, Observer {
            val fragment = when (detailsContainerViewModel.openDetailMode.value) {
                OpenDetailMode.VIEW -> DetailsFactory.viewDetail(it)
                else -> DetailsFactory.editDetail(it)
            }
            replaceFragment(fragment, fragmentContainerResourceId)
        })
    }

    // BackToBackpack overrides method already defined in details
    override fun backToEvent() {
        supportFragmentManager.popBackStack()
    }

    override fun onDetailFinished(detail: Detail) {
        detailsContainerViewModel.saveCurrentDetail(userViewModel.user.value!!, detail)
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.PHOTO),
            fragmentContainerResourceId
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startAudioDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.AUDIO),
            fragmentContainerResourceId
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.DRAWING),
            fragmentContainerResourceId
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startTextDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.TEXT),
            fragmentContainerResourceId
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startExternalFileDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.EXTERNAL_FILE),
            fragmentContainerResourceId
        )
        setLayoutListenersOnNewDetailOpened()
    }

    override fun startYoutubeDetailFragment() {
        replaceFragment(
            DetailsFactory.createDetail(DetailType.YOUTUBE),
            fragmentContainerResourceId
        )
        setLayoutListenersOnNewDetailOpened()
    }

    protected open fun setLayoutListenersOnNewDetailOpened() {
        detailsContainerViewModel.setOpenDetailType(OpenDetailMode.NEW)
    }

    override fun closeScreen() {
        closeActivitySpecificScopes()
        finish()
    }

    private fun closeActivitySpecificScopes() {
        // Close the drawing scope so unfinished drawings aren't shown when capturing
        // a new event.
        runCatching { getKoin().getScope(KoinModules.DRAWING_SCOPE_ID) }.onSuccess {
            it.close()
        }
    }

    override fun deleteDetail(detail: Detail) {
        val dialog = DeleteDetailDialog.newInstance(detail)
        dialog.show(supportFragmentManager, "DeleteDetailDialog")
    }

    override fun onDetailDeleteClick(dialog: DialogFragment, detail: Detail) {
        detailsContainerViewModel.deleteDetail(detail)
    }

    override fun onDetailCancelClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun openDetailScreenFor(detail: Detail) {
        detailsContainerViewModel.setOpenDetailType(OpenDetailMode.EDIT)
        detailsContainerViewModel.setCurrentFileAndLoadCorrespondingFile(detail)
    }
}

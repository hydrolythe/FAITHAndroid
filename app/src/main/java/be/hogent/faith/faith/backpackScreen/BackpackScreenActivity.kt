package be.hogent.faith.faith.backpackScreen

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.*
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
        AddExternalFileFragment.ExternalFileScreenNavigation{

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
    }

    // BackToBackpack overrides method already defined in details
    override fun backToEvent() {
        supportFragmentManager.popBackStack()
        backpackViewModel.viewButtons(true)
        backpackViewModel.setDetailScreenOpen(false)
        backpackViewModel.closePopUpMenu()
    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is TextDetail -> save(detail)
            is PhotoDetail -> save(detail)
            is AudioDetail -> save(detail)
        }
        backpackViewModel.viewButtons(true)
        backpackViewModel.setDetailScreenOpen(false)
        backpackViewModel.closePopUpMenu()
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(BackpackDetailFragment.PhotoFragmentNoEmotionAvatar.newInstance(), R.id.fragment)
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
        backpackViewModel.closePopUpMenu()
    }

    override fun startAudioDetailFragment() {
        replaceFragment(BackpackDetailFragment.AudioFragmentNoEmotionAvatar.newInstance(), R.id.fragment)
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
        backpackViewModel.closePopUpMenu()
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(BackpackDetailFragment.DrawingFragmentNoEmotionAvatar.newInstance(), R.id.fragment)
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
        backpackViewModel.closePopUpMenu()
    }

    override fun startTextDetailFragment() {
        replaceFragment(BackpackDetailFragment.TextFragmentNoEmotionAvatar.newInstance(), R.id.fragment)
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
        backpackViewModel.closePopUpMenu()
    }

    override fun startVideoDetailFragment() {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun startExternalFileDetailFragment() {
        replaceFragment(BackpackDetailFragment.ExternalFileFragmentNoEmotionAvatar.newInstance(), R.id.fragment)
        backpackViewModel.viewButtons(false)
        backpackViewModel.setDetailScreenOpen(true)
        backpackViewModel.closePopUpMenu()
    }

    override fun openDetailScreenFor(detail: Detail) {
        backpackViewModel.setCurrentFile(detail)
        replaceFragment(
                BackpackDetailFragment.newInstance(detail),
                R.id.fragment
        )
        backpackViewModel.viewButtons(false)
        backpackViewModel.closePopUpMenu()
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
        backpackViewModel.deleteDetail(detail)
    }
}
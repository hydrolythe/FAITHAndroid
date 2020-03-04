package be.hogent.faith.faith.backpackScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.util.replaceFragment
import io.fotoapparat.selector.back
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.getViewModel

class BackpackScreenActivity : AppCompatActivity(), BackpackScreenFragment.BackpackDetailsNavigationListener,
    RecordAudioFragment.AudioScreenNavigation,
    DrawFragment.DrawingScreenNavigation,
    DetailFinishedListener,
    TextDetailFragment.TextScreenNavigation,
    TakePhotoFragment.PhotoScreenNavigation,
    DetailViewHolder.ExistingDetailNavigationListener {

    private lateinit var backpackViewModel: BackpackViewModel

   // private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

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
        backpackViewModel.enableUi()
    }

    override fun onDetailFinished(detail: Detail) {
        when (detail) {
            is DrawingDetail -> save(detail)
            is TextDetail -> save(detail)
            is PhotoDetail -> save(detail)
            is AudioDetail -> save(detail)
        }
        backpackViewModel.enableUi()
    }

    override fun startPhotoDetailFragment() {
        replaceFragment(BackpackDetailFragment.PhotoFragmentNoEmotionAvatar.newInstance(),R.id.fragment)
        backpackViewModel.disableScreenUi()
    }

    override fun startAudioDetailFragment() {
        replaceFragment(BackpackDetailFragment.AudioFragmentNoEmotionAvatar.newInstance(),R.id.fragment)
        backpackViewModel.disableScreenUi()
    }

    override fun startDrawingDetailFragment() {
        replaceFragment(BackpackDetailFragment.DrawingFragmentNoEmotionAvatar.newInstance(),R.id.fragment)
        backpackViewModel.disableScreenUi()
    }

    override fun startTextDetailFragment() {
        replaceFragment(
            BackpackDetailFragment.TextFragmentNoEmotionAvatar.newInstance(), R.id.fragment
        )
        backpackViewModel.disableScreenUi()
    }

    override fun startVideoDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startFileDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openDetailScreenFor(detail: Detail) {
        backpackViewModel.setCurrentFile(detail)
        replaceFragment(
            BackpackDetailFragment.newInstance(detail),
            R.id.fragment
        )
        backpackViewModel.disableScreenUi()
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

    fun save(detail : TextDetail){
        backpackViewModel.saveTextDetail(detail)
    }

    fun save(detail: PhotoDetail){
        backpackViewModel.savePhotoDetail(detail)
    }

    fun save(detail: DrawingDetail){
        backpackViewModel.saveDrawingDetail(detail)
    }

    fun save(detail: AudioDetail){
        backpackViewModel.saveAudioDetail(detail)
    }
    
    

}
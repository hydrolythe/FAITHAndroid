package be.hogent.faith.faith.backpackScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.details.DetailFinishedListener
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.emotionCapture.editDetail.DetailFragmentWithEmotionAvatar
import be.hogent.faith.faith.emotionCapture.enterEventDetails.DetailViewHolder
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventDetailsFragment
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import java.util.UUID

class BackpackScreenActivity : AppCompatActivity(), EventDetailsFragment.EventDetailsNavigationListener,
    OverviewEventsFragment.OverviewEventsNavigationListener,
    DetailFragmentWithEmotionAvatar.EditDetailNavigationListener,
    RecordAudioFragment.AudioScreenNavigation,
    DrawFragment.DrawingScreenNavigation,
    DetailFinishedListener,
    TextDetailFragment.TextScreenNavigation,
    TakePhotoFragment.PhotoScreenNavigation,
    DetailViewHolder.ExistingDetailNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backpackscreen)
    }

    override fun startDrawEmotionAvatarFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startPhotoDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startAudioDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startDrawingDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startTextDetailFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun openDetailScreenFor(detail: Detail) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startEventDetailsFragment(eventUuid: UUID) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun backToEvent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDetailFinished(detail: Detail) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
// EventDetailsFragment.EventDetailsNavigationListener
//DetailViewHolder.ExistingDetailNavigationListener
package be.hogent.faith.faith.library.eventList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.library.eventfilters.EventHasDetailTypeFilter
import be.hogent.faith.faith.loadImageIntoView
import com.bumptech.glide.RequestManager
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class EventsAdapter(private val eventListener: EventListener, private val glide: RequestManager) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private var _events = emptyList<Event>().toMutableList()
    private var _showDelete: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.event_list_content, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return _events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_events[position], _showDelete, position)
    }

    fun updateEventsList(newEvents: List<Event>, newShowDelete: Boolean) {
        val diffCallback = EventListDiffCallback(_events, newEvents, _showDelete, newShowDelete)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _events.clear()
        _events.addAll(newEvents)
        _showDelete = newShowDelete
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var avatarImage: ImageView = view.findViewById(R.id.img_avatar)
        private var eventTitle: TextView = view.findViewById(R.id.lbl_title)
        private var eventDate: TextView = view.findViewById(R.id.lbl_date)
        private var eventDesc: TextView = view.findViewById(R.id.lbl_description)
        private var hasTextDetails: ImageView = view.findViewById(R.id.btn_library_event_hasText)
        private var hasAudioDetails: ImageView = view.findViewById(R.id.btn_library_event_hasAudio)
        private var hasPhotoDetails: ImageView = view.findViewById(R.id.btn_library_event_hasPhoto)
        private var hasDrawingDetails: ImageView =
            view.findViewById(R.id.btn_library_event_hasDrawing)
        private var cardView: CardView = view.findViewById(R.id.cardView_library)
        private var deleteImage: ImageButton = view.findViewById(R.id.img_delete)
        private val textDetailFilter = EventHasDetailTypeFilter(TextDetail::class)
        private val photoDetailFilter = EventHasDetailTypeFilter(PhotoDetail::class)
        private val audioDetailFilter = EventHasDetailTypeFilter(AudioDetail::class)
        private val drawingDetailFilter = EventHasDetailTypeFilter(DrawingDetail::class)

        fun bind(event: Event, showDelete: Boolean, position: Int) {
            eventTitle.text =
                if (event.title!!.length < 50) event.title!! else "${event.title!!.substring(
                    0,
                    50
                )}..."
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val eventDateString: String = formatter.format(event.dateTime)
            eventDate.text = eventDateString

            if (event.emotionAvatar != null)
                loadImageIntoView(this.itemView.context, event.emotionAvatar!!.path, avatarImage)
            else
                avatarImage.setImageDrawable(null)

            eventDesc.text = event.notes

            hasTextDetails.visibility =
                if (textDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            hasAudioDetails.visibility =
                if (audioDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            hasPhotoDetails.visibility =
                if (photoDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            hasDrawingDetails.visibility =
                if (drawingDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            deleteImage.visibility = if (showDelete) View.VISIBLE else View.GONE

            deleteImage.setOnClickListener {
                eventListener.onEventDeleteClicked(event.uuid)
            }

            cardView.setOnClickListener {
                eventListener.onEventClicked(event.uuid)
            }
        }
    }
}
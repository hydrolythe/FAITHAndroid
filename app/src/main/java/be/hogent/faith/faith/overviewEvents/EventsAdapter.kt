package be.hogent.faith.faith.overviewEvents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.GlideApp
import be.hogent.faith.util.TAG
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import org.threeten.bp.format.DateTimeFormatter

class EventsAdapter(private val eventListener: EventListener, private val glide: RequestManager) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    var events: List<Event> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(be.hogent.faith.R.layout.event_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var avatarImage: ImageView =
            view.findViewById(be.hogent.faith.R.id.img_overviewevents_thumbnailAvatar)
        private var eventTitle: TextView =
            view.findViewById(be.hogent.faith.R.id.label_overviewevents_title)
        private var eventDate: TextView =
            view.findViewById(be.hogent.faith.R.id.label_overviewevent_eventDate)

        fun bind(event: Event) {
            eventTitle.text = event.title
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val eventDateString: String = formatter.format(event.dateTime)
            Log.d(TAG, eventDateString)
            eventDate.text = eventDateString

            event.emotionAvatar?.let {
                GlideApp.with(this.itemView.context)
                    .load(FirebaseStorage.getInstance().reference.child(it.path)) // storagereference
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImage)
            }

            itemView.setOnClickListener {
                eventListener.onEventClicked(event.uuid)
            }
        }
    }
}
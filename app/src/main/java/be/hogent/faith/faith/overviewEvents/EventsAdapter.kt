package be.hogent.faith.faith.overviewEvents

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.util.TAG
import com.bumptech.glide.request.RequestOptions
import org.threeten.bp.format.DateTimeFormatter
import com.bumptech.glide.RequestManager

class EventsAdapter(private val eventListener: EventListener, private val glide: RequestManager) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    var events: List<Event> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.event_card, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var avatarImage: ImageView = view.findViewById(R.id.img_overviewevents_thumbnailAvatar)
        var eventTitle: TextView = view.findViewById(R.id.label_overviewevents_title)
        var eventDate: TextView = view.findViewById(R.id.label_overviewevent_eventDate)

        fun bind(event: Event) {
            eventTitle.text = event.title
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val eventDateString: String = formatter.format(event.dateTime)
            Log.d(TAG, eventDateString)
            eventDate.text = eventDateString

            event.emotionAvatar?.let {
                glide
                    .load(it)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImage)
            }

            itemView.setOnClickListener {
                eventListener.onEventClicked(event.uuid)
            }
        }
    }
}
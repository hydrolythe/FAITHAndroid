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
import be.hogent.faith.faith.util.TAG
import org.threeten.bp.format.DateTimeFormatter

class EventsAdapter() : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    var events: List<Event> = emptyList()
    var eventListener: EventListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.event_card, parent, false))
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "${events.size}")
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Bind Viewholder for position $position")
        holder.bind(events[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var avatarImage: ImageView
        var eventTitle: TextView
        var eventDate: TextView

        init {
            avatarImage = view.findViewById(R.id.img_overviewevents_thumbnailAvatar)
            eventTitle = view.findViewById(R.id.label_overviewevents_title)
            eventDate = view.findViewById(R.id.label_overviewevent_eventDate)
        }

        fun bind(event: Event) {
            Log.d(TAG, "binding ${event.title}")
            eventTitle.text = event.title
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val eventDateString: String = formatter.format(event.dateTime)
            Log.d(TAG, eventDateString)
            eventDate.text = eventDateString

            // TODO when creation of files is done
            /*
            Glide.with(itemView.context)
                .load(event.emotionAvatar)
                .apply(RequestOptions.circleCropTransform())
                .into(avatarImage)
            */

            itemView.setOnClickListener {
                eventListener?.onEventClicked(event.uuid)
            }
        }
    }
}
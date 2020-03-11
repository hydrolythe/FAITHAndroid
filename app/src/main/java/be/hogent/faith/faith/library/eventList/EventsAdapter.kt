package be.hogent.faith.faith.library.eventList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.loadFirestorageImage
import com.bumptech.glide.RequestManager
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class EventsAdapter(private val eventListener: EventListener, private val glide: RequestManager) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    var events: List<Event> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.event_list_content, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(events[position], position)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var avatarImage: ImageView = view.findViewById(R.id.img_avatar)
        private var eventTitle: TextView = view.findViewById(R.id.lbl_title)
        private var eventDate: TextView = view.findViewById(R.id.lbl_date)
        private var eventDesc: TextView = view.findViewById(R.id.lbl_description)

        fun bind(event: Event, position: Int) {
            eventTitle.text =
                if (event.title!!.length < 50) event.title!! else "${event.title!!.substring(
                    0,
                    50
                )}..."
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val eventDateString: String = formatter.format(event.dateTime)
            eventDate.text = eventDateString

            if (event.emotionAvatar != null)
                loadFirestorageImage(this.itemView.context, event.emotionAvatar!!.path, avatarImage)
            else
                avatarImage.setImageDrawable(null)

            eventDesc.text = event.notes

            itemView.setOnClickListener {
                // eventListener.onEventClicked(position)
            }
        }
    }
}
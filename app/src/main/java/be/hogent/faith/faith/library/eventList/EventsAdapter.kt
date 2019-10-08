package be.hogent.faith.faith.library.eventList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import org.threeten.bp.format.DateTimeFormatter

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
        private var expandButton: ImageButton = view.findViewById(R.id.expand_button)


        fun bind(event: Event, position: Int) {
            eventTitle.text = event.title
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val eventDateString: String = formatter.format(event.dateTime)
            eventDate.text = eventDateString

            event.emotionAvatar?.let {
                glide
                    .load(it)
                    .apply(RequestOptions.circleCropTransform())
                    .into(avatarImage)
            }

            eventDesc.text = event.notes

            expandButton.setOnClickListener {
                if (eventDesc.visibility == View.GONE) {
                    eventDesc.visibility = View.VISIBLE
                } else {
                    eventDesc.visibility = View.GONE
                }
            }

            itemView.setOnClickListener {
                eventListener.onEventClicked(position)
            }
        }
    }
}
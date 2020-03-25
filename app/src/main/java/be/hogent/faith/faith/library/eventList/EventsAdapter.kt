package be.hogent.faith.faith.library.eventList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import be.hogent.faith.R
import be.hogent.faith.databinding.LibraryRvItemBinding
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.library.eventfilters.EventHasDetailTypeFilter
import be.hogent.faith.faith.loadImageIntoView
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class EventsAdapter(private val eventListener: EventListener) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    private var _events = emptyList<Event>().toMutableList()
    private var _showDelete: Boolean = false
    private val textDetailFilter = EventHasDetailTypeFilter(TextDetail::class)
    private val photoDetailFilter = EventHasDetailTypeFilter(PhotoDetail::class)
    private val audioDetailFilter = EventHasDetailTypeFilter(AudioDetail::class)
    private val drawingDetailFilter = EventHasDetailTypeFilter(DrawingDetail::class)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater
            .from(parent.context)
        val binding: LibraryRvItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.library_rv_item, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return _events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(_events[position], _showDelete)
    }

    fun updateEventsList(newEvents: List<Event>, newShowDelete: Boolean) {
        val diffCallback = EventListDiffCallback(_events, newEvents, _showDelete, newShowDelete)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _events.clear()
        _events.addAll(newEvents)
        _showDelete = newShowDelete
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(val itemBinding: LibraryRvItemBinding) :
        RecyclerView.ViewHolder(itemBinding.getRoot()) {

        fun bind(event: Event, showDelete: Boolean) {
            itemBinding.lblTitle.text =
                if (event.title!!.length < 30) event.title!! else "${event.title!!.substring(
                    0,
                    30
                )}..."
            itemBinding.lblDescription.text = event.notes
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val eventDateString: String = formatter.format(event.dateTime)
            itemBinding.lblDate.text = eventDateString

            if (event.emotionAvatar != null)
                loadImageIntoView(
                    this.itemView.context,
                    event.emotionAvatar!!.path,
                    itemBinding.imgAvatar
                )
            else
                itemBinding.imgAvatar.setImageDrawable(null)

            itemBinding.btnLibraryEventHasText.visibility =
                if (textDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            itemBinding.btnLibraryEventHasAudio.visibility =
                if (audioDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            itemBinding.btnLibraryEventHasPhoto.visibility =
                if (photoDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            itemBinding.btnLibraryEventHasDrawing.visibility =
                if (drawingDetailFilter.invoke(event)) View.VISIBLE else View.GONE
            itemBinding.imgDelete.visibility = if (showDelete) View.VISIBLE else View.GONE

            itemBinding.imgDelete.setOnClickListener {
                eventListener.onEventDeleteClicked(event.uuid)
            }

            itemBinding.cardViewLibrary.setOnClickListener {
                eventListener.onEventClicked(event.uuid)
            }
        }
    }
}
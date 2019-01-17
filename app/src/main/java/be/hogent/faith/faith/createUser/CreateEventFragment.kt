package be.hogent.faith.faith.createUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_create_event.*
import org.threeten.bp.LocalDateTime

class CreateEventFragment : Fragment() {

    private lateinit var viewModel: CreateEventViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!).get(CreateEventViewModel::class.java)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        viewModel.eventDescription.observe(this, Observer {
            text_createEvent_description.text = it
        })
        // TODO: move to VM once databinding is used
        button_createEvent_create.setOnClickListener {
            viewModel.createEvent.execute(LocalDateTime.now(), "testDescription")
        }
    }
}
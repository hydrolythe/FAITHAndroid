package be.hogent.faith.faith.createUser

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_create_event.*
import org.koin.android.viewmodel.ext.android.viewModel

class CreateEventFragment : Fragment() {

    private val createEventViewModel: CreateEventViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        createEventViewModel.eventDescription.observe(this, Observer {
            Log.d(TAG, "Received event description: $it")
            text_createEvent_description.text = it
        })
    }


    companion object {
        const val TAG = "CreateEventFragment"
    }
}
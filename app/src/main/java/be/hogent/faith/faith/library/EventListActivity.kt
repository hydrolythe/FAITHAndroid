package be.hogent.faith.faith.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R

/**
 * An activity representing a list of Events of the user. This activity
 * has different presentations for small and bigger devices. On
 * smaller devices, the activity presents a list of events, which when touched,
 * lead to a [EventDetailActivity] representing
 * event details. On nbigger devices, the activity presents the list of events and
 * events details side-by-side using two vertical panes.
 */
class EventListActivity : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventlist)


        //setupRecyclerView(rv_eventlist)
    }




}

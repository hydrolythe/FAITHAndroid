package be.hogent.faith.faith.createUser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.service.usecases.CreateEvent
import be.hogent.faith.service.usecases.GetEvents
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CreateEventViewModel : ViewModel() {
    @Inject
    lateinit var getEvents: GetEvents

    @Inject
    lateinit var createEvent: CreateEvent

    val eventDescription = MutableLiveData<String>()

    init {
        val eventObserver = getEvents.execute()
            .subscribeOn(Schedulers.io())
            .doOnNext { it -> eventDescription.value = it[0].description }
    }
}
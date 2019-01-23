package be.hogent.faith.faith.createUser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.service.usecases.CreateEventUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CreateEventViewModel : ViewModel() {
    @Inject
    lateinit var getEvents: GetEventsUseCase

    @Inject
    lateinit var createEvent: CreateEventUseCase

    val eventDescription = MutableLiveData<String>()

    init {
        createEvent.
    }
}
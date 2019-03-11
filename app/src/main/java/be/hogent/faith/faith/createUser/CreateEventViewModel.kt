package be.hogent.faith.faith.createUser

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.service.usecases.CreateEventUseCase
import be.hogent.faith.service.usecases.GetEventsUseCase
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDateTime

class CreateEventViewModel(
    private val getEventsUseCase: GetEventsUseCase,
    private val createEventUseCase: CreateEventUseCase
) : ViewModel() {

    val eventDescription = MutableLiveData<String>()

    /**
     * A container for all the disposables made during the lifetime of this [CreateEventViewModel].
     * Every observer should add itself to this disposable so that all can be disposed of during [onCleared]
     */
    private val disposables = CompositeDisposable()

    init {
        val createEventParams = CreateEventUseCase.Params(LocalDateTime.now(), "test")
        val createEventCompletable = createEventUseCase.execute(createEventParams)
            .subscribe({
                Log.d(TAG, "Created event successfully")
            }, {

                Log.d(TAG, "Failed to create event")
            })
        disposables.add(createEventCompletable)

        val getEventsCompletable = getEventsUseCase.execute(null)
            .subscribe {
                eventDescription.postValue(it.first().title)
            }
        disposables.add(getEventsCompletable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
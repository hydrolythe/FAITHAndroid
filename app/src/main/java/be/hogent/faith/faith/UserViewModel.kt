package be.hogent.faith.faith

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.PictureDetail
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.service.usecases.GetUserUseCase
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.util.UUID

class UserViewModel(private val getUserUseCase: GetUserUseCase) : ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    val user: LiveData<User>
        get() = _user

    private val disposables = CompositeDisposable()

    init {
        _user.postValue(createUser())
        Log.d(TAG, "User in UserViewmodel created")
        // TODO replace hardcoded user
        /*
         val getUserCompletable = getUserUseCase.execute(GetUserUseCase.Params(uuid))
            .subscribe{
                Log.d(TAG, "user ${it.uuid} fetched")
                _user.postValue(it)
            }
        disposables.add(getUserCompletable)
        */
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    private fun createUser(): User {
        val userUuid = UUID.randomUUID()
        val eventDate = LocalDateTime.of(2019, 10, 28, 7, 33)!!
        val file = File("path/to/eventFile")

        val user = User(userUuid)

        val event1 = Event(eventDate, "testDescription1", file, UUID.randomUUID())
        val event2 = Event(eventDate, "testDescription2", file, UUID.randomUUID())

        val detail1 = PictureDetail(file)
        val detail2 = AudioDetail(file)

        event1.addDetail(detail1)
        event1.addDetail(detail2)
        user.addEvent(event1)
        user.addEvent(event2)
        return user
    }
}
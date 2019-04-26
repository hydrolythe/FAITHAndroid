package be.hogent.faith.faith

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.User
import be.hogent.faith.service.usecases.GetUserUseCase
import be.hogent.faith.util.TAG
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDateTime
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

        val user = User(uuid = userUuid, username = "dikke guy")
        // TODO: add avatar again once it's done
//        val user = User(uuid = userUuid, username = "dikke guy", avatar = Avatar(-1, "url"))

        val event1 = Event(eventDate, "testDescription1", file, "notities", UUID.randomUUID())
        val event2 = Event(eventDate, "testDescription2", file, "nog notities", UUID.randomUUID())

        event1.addNewAudioDetail(file, "testName")
        event1.addNewAudioDetail(file, "testAudio")
        user.addEvent(event1)
        user.addEvent(event2)
        return user
    }
}
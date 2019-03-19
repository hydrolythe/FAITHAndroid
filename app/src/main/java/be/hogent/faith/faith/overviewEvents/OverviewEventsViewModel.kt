package be.hogent.faith.faith.overviewEvents

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.User

class OverviewEventsViewModel(val user: LiveData<User>) : ViewModel()

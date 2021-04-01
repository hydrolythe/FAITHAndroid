package be.hogent.faith.faith.skyscraper.startscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.goals.Goal
import be.hogent.faith.faith.util.LoadingViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class SkyscraperOverviewViewModel(
    private val user: User
) : LoadingViewModel() {
    private val _goals = MutableLiveData<List<Goal>>().apply {
        value = emptyList()
    }
    val goals: LiveData<List<Goal>> =
//         Add a sort so skyscrapers can't switch places when updated
        Transformations.map(_goals) { goals -> goals.sortedBy { it.dateTime } }

    private val _goalSavedSuccessfully = SingleLiveEvent<Unit>()
    val goalSavedSuccessfully: LiveData<Unit> = _goalSavedSuccessfully

    private val _errorMessage = SingleLiveEvent<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    init {
        loadGoals()
    }

    private fun loadGoals() {
        startLoading()

    }

    fun addNewGoal() {
        startLoading()

    }

    // TODO : loading terug implementeren. Vogeltje blijft staat
    fun updateGoalDescription(goal: Goal, newDescription: String) {
        goal.description = newDescription
        //  startLoading()

    }
}
package be.hogent.faith.faith.skyscraper.startscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.goal.GetGoalsUseCase
import be.hogent.faith.service.usecases.goal.SaveGoalUseCase
import be.hogent.faith.service.usecases.goal.UpdateGoalUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.subscribers.DisposableSubscriber
import timber.log.Timber

class SkyscraperOverviewViewModel(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val saveGoalUseCase: SaveGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val user: User
) : ViewModel() {
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
        val params = GetGoalsUseCase.Params(user)
        getGoalsUseCase.execute(params, object : DisposableSubscriber<List<Goal>>() {
            override fun onComplete() {}

            override fun onNext(goals: List<Goal>?) {
                _goals.value = goals
            }

            override fun onError(error: Throwable?) {
                Timber.e(error)
                _errorMessage.postValue(R.string.error_skyscraper_load_goals_failed)
            }
        })
    }

    fun addNewGoal() {
        user.addGoal()
        val params = SaveGoalUseCase.Params(user.activeGoals.last())
        saveGoalUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _goalSavedSuccessfully.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_skyscraper_add_goal_failed)
            }
        })
    }

    fun updateGoalDescription(goal: Goal, newDescription: String) {
        goal.description = newDescription
        val params = UpdateGoalUseCase.Params(goal)
        updateGoalUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                Timber.i("Goal ${goal.uuid} description updated to ${goal.description}")
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_skyscraper_add_goal_failed)
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        getGoalsUseCase.dispose()
        saveGoalUseCase.dispose()
    }
}
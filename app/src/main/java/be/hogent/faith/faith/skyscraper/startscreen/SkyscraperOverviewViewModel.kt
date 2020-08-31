package be.hogent.faith.faith.skyscraper.startscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import be.hogent.faith.R
import be.hogent.faith.domain.models.User
import be.hogent.faith.domain.models.exceptions.MaxNumberOfGoalsReachedException
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.faith.util.LoadingViewModel
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.goal.AddNewGoalUseCase
import be.hogent.faith.service.usecases.goal.GetGoalsUseCase
import be.hogent.faith.service.usecases.goal.SaveGoalUseCase
import be.hogent.faith.service.usecases.goal.UpdateGoalUseCase
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.subscribers.DisposableSubscriber
import timber.log.Timber

class SkyscraperOverviewViewModel(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val saveGoalUseCase: SaveGoalUseCase,
    private val addNewGoalUseCase: AddNewGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase,
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
        val params = GetGoalsUseCase.Params(user)
        startLoading()
        getGoalsUseCase.execute(params, object : DisposableSubscriber<List<Goal>>() {
            override fun onComplete() {
                doneLoading()
            }

            override fun onNext(goals: List<Goal>?) {
                _goals.value = goals
                doneLoading()
            }

            override fun onError(error: Throwable?) {
                Timber.e(error)
                _errorMessage.postValue(R.string.error_skyscraper_load_goals_failed)
                doneLoading()
            }
        })
    }

    fun addNewGoal() {
        startLoading()
        val params = AddNewGoalUseCase.Params(user)
        addNewGoalUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                _goalSavedSuccessfully.call()
                doneLoading()
            }

            override fun onError(e: Throwable) {
                if (e is MaxNumberOfGoalsReachedException) {
                    _errorMessage.postValue(R.string.error_skyscraper_max_number_of_goals_reached)
                } else {
                    _errorMessage.postValue(R.string.error_skyscraper_add_goal_failed)
                }
                doneLoading()
            }
        })
    }

    // TODO : loading terug implementeren. Vogeltje blijft staat
    fun updateGoalDescription(goal: Goal, newDescription: String) {
        goal.description = newDescription
        val params = UpdateGoalUseCase.Params(goal)
        //  startLoading()
        updateGoalUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                Timber.i("Goal ${goal.uuid} description updated to ${goal.description}")
                //  doneLoading()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_skyscraper_add_goal_failed)
                // doneLoading()
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        getGoalsUseCase.dispose()
        saveGoalUseCase.dispose()
        updateGoalUseCase.dispose()
        addNewGoalUseCase.dispose()
    }
}
package be.hogent.faith.faith.skyscraper.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.R
import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.ReachGoalWay
import be.hogent.faith.domain.models.goals.SubGoal
import be.hogent.faith.faith.util.SingleLiveEvent
import be.hogent.faith.service.usecases.goal.UpdateGoalUseCase
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import timber.log.Timber

class GoalViewModel(
    private val updateGoalUseCase: UpdateGoalUseCase,
    givenGoal: Goal
) : ViewModel() {
    val goal = MediatorLiveData<Goal>()
    val subgoals: LiveData<Map<Int, SubGoal>> = Transformations.map(goal) { it.subGoals }

    val selectedSubGoal = MediatorLiveData<Pair<Int, SubGoal>?>()
    val actions: LiveData<List<Action>?> =
        Transformations.map(selectedSubGoal) { it?.second?.actions }
    val selectedSubGoalDescription = MutableLiveData<String>()

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit> = _cancelButtonClicked

    private val _goalSavedSuccessfully = SingleLiveEvent<Unit>()
    val goalSavedSuccessfully: LiveData<Unit> = _goalSavedSuccessfully

    private val _errorMessage = SingleLiveEvent<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    init {
        goal.value = givenGoal
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        updateCurrentSelectedSubGoal()
        val params = UpdateGoalUseCase.Params(goal.value!!)
        updateGoalUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                Timber.i("Goal ${goal.value!!.uuid} updated")
                _goalSavedSuccessfully.call()
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_skyscraper_update_goal_failed)
            }
        })
    }

    fun onSelectSubGoal(index: Int) {
            updateCurrentSelectedSubGoal()
            if (goal.value!!.subGoals.containsKey(index))
                selectedSubGoal.value = Pair(index, goal.value!!.subGoals[index]!!)
            else
                selectedSubGoal.value = Pair(index, SubGoal(""))
            selectedSubGoalDescription.value = selectedSubGoal.value!!.second.description
    }

    private fun updateCurrentSelectedSubGoal() {
        selectedSubGoal.value?.let {
            it.second.description = selectedSubGoalDescription.value!!
            selectedSubGoal.value = selectedSubGoal.value
            goal.value!!.addSubGoal(
                selectedSubGoal.value!!.second,
                selectedSubGoal.value!!.first
            )
        }
        goal.value = goal.value
    }

    fun removeAction(position: Int) {
        selectedSubGoal.value?.let {
            it.second.removeAction(it.second.actions.get(position))
        }
        selectedSubGoal.value = selectedSubGoal.value
    }

    fun moveAction(fromPosition: Int, toPosition: Int) {
        selectedSubGoal.value?.second?.updateActionPosition(fromPosition, toPosition)
        selectedSubGoal.value = selectedSubGoal.value
    }

    fun addNewAction() {
        if (selectedSubGoal.value != null) {
            if (selectedSubGoalDescription.value.isNullOrEmpty())
                _errorMessage.value = R.string.subdoel_naam_verplicht
            else {
                selectedSubGoal.value?.second?.addAction(Action())
                selectedSubGoal.value = selectedSubGoal.value
            }
        }
    }

    fun updateAction(position: Int, description: String) {
        if (selectedSubGoal.value!!.second.actions.get(position).description != description)
            selectedSubGoal.value!!.second.updateAction(position, description)
    }

    fun setPositionAvatar(position: Int) {
        if (goal.value!!.currentPositionAvatar != position) {
            goal.value!!.currentPositionAvatar = position
            goal.value = goal.value
        }
    }

    fun setReachGoalWay(reachGoalWay: ReachGoalWay) {
        if (goal.value!!.chosenReachGoalWay != reachGoalWay) {
            goal.value!!.chosenReachGoalWay = reachGoalWay
            goal.value = goal.value
        }
    }

    fun setCompleted() {
        goal.value!!.toggleCompleted()
        goal.value = goal.value
    }

    override fun onCleared() {
        super.onCleared()
        updateGoalUseCase.dispose()
    }
}
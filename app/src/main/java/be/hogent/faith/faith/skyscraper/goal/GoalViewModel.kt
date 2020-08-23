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

    private val _onAvatarPlaceChanged = SingleLiveEvent<Unit>()
    val onAvatarPlaceChanged: LiveData<Unit> = _onAvatarPlaceChanged

    private val _errorMessage = SingleLiveEvent<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = _isLoading

    val numberOfFloorsUpperBound = 9

    init {
        goal.value = givenGoal
        _onAvatarPlaceChanged.call()
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        updateCurrentSelectedSubGoal()
        val params = UpdateGoalUseCase.Params(goal.value!!)
        _isLoading.value = true
        updateGoalUseCase.execute(params, object : DisposableCompletableObserver() {
            override fun onComplete() {
                Timber.i("Goal ${goal.value!!.uuid} updated")
                _goalSavedSuccessfully.call()
                _isLoading.value = false
            }

            override fun onError(e: Throwable) {
                _errorMessage.postValue(R.string.error_skyscraper_update_goal_failed)
                _isLoading.value = false
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

    fun updateCurrentSelectedSubGoal() {
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
        selectedSubGoal.value?.second?.removeAction(position)
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
                updateCurrentSelectedSubGoal()
                selectedSubGoal.value?.second?.addAction(Action())
                selectedSubGoal.value = selectedSubGoal.value
            }
        }
    }

    fun updateAction(position: Int, description: String) {
        if (actionDescriptionHasChanged(position, description))
            selectedSubGoal.value!!.second.updateAction(position, description)
    }

    fun updateActionState(position: Int) {
        selectedSubGoal.value!!.second.updateActionStatus(position)
        selectedSubGoal.value = selectedSubGoal.value
    }

    private fun actionDescriptionHasChanged(position: Int, description: String): Boolean {
        Timber.i("actiondescr $position and $description")
        return selectedSubGoal.value!!.second.actions.size > position && selectedSubGoal.value!!.second.actions[position].description != description
    }

    private fun positionAvatarHasChanged(position: Int): Boolean {
        return (goal.value!!.currentPositionAvatar != position)
    }

    private fun reachGoalWayHasChanged(reachGoalWay: ReachGoalWay): Boolean {
        return goal.value!!.chosenReachGoalWay != reachGoalWay
    }

    fun setPositionAvatar(position: Int) {
        if (positionAvatarHasChanged(position)) {
            goal.value!!.currentPositionAvatar = position
            goal.value = goal.value
        }
    }

    fun setReachGoalWay(reachGoalWay: ReachGoalWay) {
        if (reachGoalWayHasChanged(reachGoalWay)) {
            if (goal.value!!.currentPositionAvatar < 0 && reachGoalWay != ReachGoalWay.Stairs) {
                _errorMessage.value = R.string.error_skyscraper_update_goalreach
            } else {
                goal.value!!.chosenReachGoalWay = reachGoalWay
                goal.value = goal.value
                _onAvatarPlaceChanged.call()
            }
        }
    }

    fun setCompleted() {
        goal.value!!.toggleCompleted()
        goal.value = goal.value
        _onAvatarPlaceChanged.call()
    }

    fun moveSubGoal(fromPosition: Int, toPosition: Int) {
        subgoals.value?.let {
            selectedSubGoal.value = null
            goal.value!!.changeFloorSubGoal(fromPosition, toPosition)
            goal.value = goal.value
        }
    }

    fun removeSubGoal(position: Int) {
        subgoals.value?.get(position)?.let {
            selectedSubGoal.value = null
            goal.value!!.removeSubGoal(it)
        }
        goal.value = goal.value
    }

    override fun onCleared() {
        super.onCleared()
        updateGoalUseCase.dispose()
    }
}
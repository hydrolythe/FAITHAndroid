package be.hogent.faith.faith.skyscraper.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import be.hogent.faith.R
import be.hogent.faith.faith.models.goals.ReachGoalWay
import be.hogent.faith.faith.models.User
import be.hogent.faith.faith.models.goals.Action
import be.hogent.faith.faith.models.goals.Goal
import be.hogent.faith.faith.models.goals.SubGoal
import be.hogent.faith.faith.util.LoadingViewModel
import be.hogent.faith.faith.util.SingleLiveEvent

class GoalViewModel(
    givenGoal: Goal,
    private val user: User
) : LoadingViewModel() {
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
        startLoading()

    }

    fun onSelectSubGoal(index: Int) {
        // updateCurrentSelectedSubGoal()
        if (goal.value!!.subGoals.containsKey(index))
            selectedSubGoal.value = Pair(index, goal.value!!.subGoals[index]!!)
        else
            selectedSubGoal.value = Pair(index, SubGoal(""))
        selectedSubGoalDescription.value = selectedSubGoal.value!!.second.description
    }

    fun updateCurrentSelectedSubGoal() {
        selectedSubGoal.value?.let {
            it.second.description = selectedSubGoalDescription.value!!
            goal.value!!.addSubGoal(
                selectedSubGoal.value!!.second,
                selectedSubGoal.value!!.first
            )
        }
        goal.value = goal.value
    }

    fun removeAction(position: Int) {
        selectedSubGoal.value?.second?.removeAction(getActionIndex(position))
        selectedSubGoal.value = selectedSubGoal.value
    }

    fun moveAction(fromPosition: Int, toPosition: Int) {
        selectedSubGoal.value?.second?.updateActionPosition(getActionIndex(fromPosition), getActionIndex(toPosition))
        selectedSubGoal.value = selectedSubGoal.value
    }

    fun addNewAction() {
        if (selectedSubGoal.value != null) {
            if (selectedSubGoalDescription.value.isNullOrEmpty())
                _errorMessage.value = R.string.subdoel_naam_verplicht
            else {
                // if (goal.value!!.subGoals[selectedSubGoal.value!!.first] == null || selectedSubGoalDescription.value != goal.value!!.subGoals[selectedSubGoal.value!!.first]!!.description)
                // updateCurrentSelectedSubGoal()
                selectedSubGoal.value?.second?.addAction(Action())
                selectedSubGoal.value = selectedSubGoal.value
            }
        }
    }

    fun updateAction(position: Int, description: String) {
        if (selectedSubGoal.value!!.first == getGoalIndex(position)) {
            if (actionDescriptionHasChanged(getActionIndex(position), description)) {
                selectedSubGoal.value!!.second.updateAction(getActionIndex(position), description)
            }
        } else {
            val subgoalForAction = goal.value!!.subGoals[getGoalIndex(position)]
            subgoalForAction?.updateAction(getActionIndex(position), description)
        }
    }

    fun updateActionState(position: Int) {
        selectedSubGoal.value!!.second.updateActionStatus(getActionIndex(position))
        selectedSubGoal.value = selectedSubGoal.value
    }

    private fun actionDescriptionHasChanged(position: Int, description: String): Boolean {
        return selectedSubGoal.value!!.second.actions.size > getActionIndex(position) && selectedSubGoal.value!!.second.actions[getActionIndex(position)].description != description
    }

    private fun positionAvatarHasChanged(position: Int): Boolean {
        return (goal.value!!.currentPositionAvatar != getActionIndex(position))
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

    // position contains 2 numbers : 1ste : index of the subgoal, 2nd index of the action
    private fun getGoalIndex(position: Int): Int {
        return position / 10
    }

    // position contains 2 numbers : 1ste : index of the subgoal, 2nd index of the action
    private fun getActionIndex(position: Int): Int {
        return position % 10
    }

    fun setCompleted() {
        goal.value!!.toggleCompleted()
        goal.value = goal.value
        _onAvatarPlaceChanged.call()
    }

    fun saveSubGoal() {
        updateCurrentSelectedSubGoal()
    }

    fun moveSubGoal(fromPosition: Int, toPosition: Int) {
        //   updateCurrentSelectedSubGoal()
        selectedSubGoal.value = null
        goal.value!!.changeFloorSubGoal(fromPosition, toPosition)
        goal.value = goal.value
    }

    fun removeSubGoal(position: Int) {
        subgoals.value?.get(position)?.let {
            selectedSubGoal.value = null
            goal.value!!.removeSubGoal(it)
        }
        goal.value = goal.value
    }
}
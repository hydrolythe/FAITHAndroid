package be.hogent.faith.faith.skyscraper.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.SubGoal
import be.hogent.faith.faith.util.SingleLiveEvent

class GoalViewModel(
    givenGoal: Goal
) : ViewModel() {
    val goal = MediatorLiveData<Goal>()
    val subgoals: LiveData<Map<Int, SubGoal>> = Transformations.map(goal) { it.subGoals }

    val selectedSubGoal = MediatorLiveData<Pair<Int, SubGoal>?>()
    val actions: LiveData<List<Action>?> = Transformations.map(selectedSubGoal) { it?.second?.actions }
    val selectedSubGoalDescription = MutableLiveData<String>()

    private val _cancelButtonClicked = SingleLiveEvent<Unit>()
    val cancelButtonClicked: LiveData<Unit> = _cancelButtonClicked

    init {
        goal.value = givenGoal
    }

    fun onCancelButtonClicked() {
        _cancelButtonClicked.call()
    }

    fun onSaveButtonClicked() {
        updateCurrentSelectedSubGoal()
        // TODO update goal in database
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
            goal.value!!.changeFloorSubGoal(selectedSubGoal.value!!.second, selectedSubGoal.value!!.first)
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
        selectedSubGoal.value?.second?.addAction(Action())
        selectedSubGoal.value = selectedSubGoal.value
    }

    fun updateAction(position: Int, description: String) {
        selectedSubGoal.value?.second?.updateAction(position, description)
        selectedSubGoal.value = selectedSubGoal.value
    }
}
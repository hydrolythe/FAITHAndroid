package be.hogent.faith.domain.models.goals

import java.util.UUID
import org.threeten.bp.LocalDateTime

const val MAX_AMOUNT_OF_SUBGOALS = 10

enum class ReachGoalWay {
    Stairs, Elevator, Rope
}

class Goal(
    val uuid: UUID = UUID.randomUUID(),
    private var description: String,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    private var isCompleted: Boolean = false,
    private var currentPositionAvatar: Int = 0
) {
    private var chosenReachGoalWay: ReachGoalWay = ReachGoalWay.Elevator

    private var _subGoals = mutableListOf<SubGoal>()
    val subGoals: List<SubGoal>
        get() = _subGoals

    fun changeSubGoalPosition(subGoalToUpdate: SubGoal, newPosition: Int) {
        _subGoals.remove(subGoalToUpdate)
        _subGoals.add(newPosition, subGoalToUpdate)
        updateSubGoalPositions()
    }

    private fun updateSubGoalPositions() {
        for (goal in _subGoals)
            goal.changeCurrentPosition(_subGoals.indexOf(goal))
    }

    fun addSubGoalToGoal(newSubGoal: SubGoal) {
        checkIfGoalIsCompleted()
            if (subGoals.size >= MAX_AMOUNT_OF_SUBGOALS)
                throw IllegalArgumentException("Reached maximum amount of subgoals")
        else {
                newSubGoal.currentPosition = _subGoals.size
                _subGoals.add(newSubGoal)
            }
    }

    fun removeSubGoalFromGoal(subGoal: SubGoal) {
        checkIfGoalIsCompleted()
            if (_subGoals.contains(subGoal))
                _subGoals.remove(subGoal)
            else
                throw IllegalArgumentException("Unable to find the selected SubGoal")
    }

    private fun checkIfGoalIsCompleted() {
        if (isCompleted) throw IllegalArgumentException("Goal is completed")
    }

    fun moveAvatarToSubGoal(subGoal: SubGoal) {
        currentPositionAvatar = subGoal.currentPosition
    }

    fun editDescription(newDescription: String) {
        if (newDescription.length <= 30)
        description = newDescription
    else
    throw IllegalArgumentException("Description > 30 characters") }

    fun setIsCompleted() { isCompleted = !isCompleted }
    fun changeReachGoalWay(newReachGoalWay: ReachGoalWay) { chosenReachGoalWay = newReachGoalWay }
}

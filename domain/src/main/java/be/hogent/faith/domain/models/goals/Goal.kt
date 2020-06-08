package be.hogent.faith.domain.models.goals

import org.threeten.bp.LocalDateTime
import java.util.UUID

/**
 * -1 indicates the basement
 */
private const val SUBGOALS_LOWER_BOUND = -1

// There are 10 floors, including the ground floor, making 9 the top floor.
private const val SUBGOALS_UPPER_BOUND = 9
private const val DESCRIPTION_MAX_LENGTH = 30

// TODO change to value of the colors
enum class GoalColor {
    RED, GREEN, YELLOW, BLUE, PURPLE
}

/**
 * The possible options of where the avatar can be placed, indicating the different ways to
 * reach his goal.
 */
enum class ReachGoalWay {
    Stairs, Elevator, Rope
}

class Goal(
    val goalColor: GoalColor,
    val uuid: UUID = UUID.randomUUID()
) {
    var dateTime: LocalDateTime = LocalDateTime.now()

    var isCompleted: Boolean = false

    var currentPositionAvatar: Int = 0

    val _subGoals = mutableMapOf<Int, SubGoal>()
    val subGoals: Map<Int, SubGoal> = _subGoals

    var description: String = ""
        set(value) {
            require(value.length <= DESCRIPTION_MAX_LENGTH) { "Beschrijving mag niet langer zijn dan $DESCRIPTION_MAX_LENGTH tekens." }
            field = value
        }

    var chosenReachGoalWay: ReachGoalWay = ReachGoalWay.Elevator

    fun addSubGoal(newSubGoal: SubGoal, floor: Int) {
        require((SUBGOALS_LOWER_BOUND..SUBGOALS_UPPER_BOUND).contains(floor))
        _subGoals[floor] = newSubGoal
    }

    fun removeSubGoal(subGoal: SubGoal) {
        val floor = findFloorForSubgoal(subGoal)
        _subGoals.remove(floor)
    }

    fun changeFloorSubGoal(subGoal: SubGoal, floor: Int) {
        val currentGoalAtFloor = _subGoals[floor]
        if (currentGoalAtFloor == null) {
            removeSubGoal(subGoal)
            _subGoals[floor] = subGoal
        } else {
            val originalFloor = findFloorForSubgoal(subGoal)
            removeSubGoal(subGoal)
            _subGoals[floor] = subGoal
            _subGoals[originalFloor] = currentGoalAtFloor
        }
    }

    internal fun toggleCompleted() {
        isCompleted = !isCompleted
    }

    fun moveAvatarToSubGoal(subGoal: SubGoal) {
        currentPositionAvatar = findFloorForSubgoal(subGoal)
    }

    private fun findFloorForSubgoal(subGoal: SubGoal): Int {
        _subGoals.entries.forEach { (index, goal) ->
            if (goal == subGoal) {
                return index
            }
        }
        throw NoSuchElementException()
    }
}

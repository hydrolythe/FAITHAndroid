package be.hogent.faith.domain.models.goals

import org.threeten.bp.LocalDateTime
import java.util.UUID

private const val MAX_AMOUNT_OF_SUBGOALS = 10
private const val MAX_CHARACTERS_GOAL_DESCRIPTION = 30

/**
 * The possible options of where the avatar can be placed, indicating the different ways to
 * reach his goal.
 */
enum class ReachGoalWay {
    Stairs, Elevator, Rope
}

class Goal(
    val uuid: UUID = UUID.randomUUID(),
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var isCompleted: Boolean = false,
    var currentPositionAvatar: Int = 0,
    val color : Int,
    var subGoals: Array<SubGoal?> = arrayOfNulls(MAX_AMOUNT_OF_SUBGOALS)
) {

    var description: String = ""
        set(value) {
            require(value.length <= MAX_CHARACTERS_GOAL_DESCRIPTION) { "Beschrijving mag niet langer zijn dan $MAX_CHARACTERS_GOAL_DESCRIPTION tekens." }
            field = value
        }

    var chosenReachGoalWay: ReachGoalWay = ReachGoalWay.Elevator

    fun addSubGoalToGoal(newSubGoal: SubGoal, index: Int) {
        subGoals[index] = newSubGoal
    }

    fun removeSubGoalFromGoal(subGoal: SubGoal) {
        require(subGoals.contains(subGoal)) { "Subdoel is niet aanwezig in de lijst" }
        val subGoalsList = subGoals.toMutableList()
        subGoalsList.remove(subGoal)
        subGoals = subGoalsList.toTypedArray()
    }

    //Verwisselen? of moet alles echt een positie opschuiven
    fun changeIndexSubGoal(subGoal: SubGoal, newIndex: Int) {
        val temp = subGoals[subGoals.indexOf(subGoal)]
        subGoals[subGoals.indexOf(subGoal)] = subGoals[newIndex]
        subGoals[newIndex] = temp
    }

    internal fun toggleCompleted() {
        isCompleted = !isCompleted
    }

    fun moveAvatarToSubGoal(subGoal: SubGoal) {
        if (subGoals.contains(subGoal))
            currentPositionAvatar = subGoals.indexOf(subGoal)
    }
}


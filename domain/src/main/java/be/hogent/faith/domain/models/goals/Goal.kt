package be.hogent.faith.domain.models.goals

import org.threeten.bp.LocalDateTime
import java.util.UUID

enum class ReachGoalWay {
    Stairs, Elevator, Rope
}

enum class ColorGoals {
    red, green, yellow
}

const val MAX_AMOUNT_OF_SUBGOALS = 10

class Goal(
    val uuid: UUID = UUID.randomUUID(),
    var description: String,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var isCompleted: Boolean = false,
    var currentPositionAvatar: Int = 0,
    val color: ColorGoals = ColorGoals.green,
    var chosenReachGoalWay: ReachGoalWay = ReachGoalWay.Elevator,
    var subGoals: Array<SubGoal?> = arrayOfNulls(MAX_AMOUNT_OF_SUBGOALS)
) {
    fun addSubGoal(subGoal: SubGoal, index: Int) {
        if (index < MAX_AMOUNT_OF_SUBGOALS)
            subGoals[index] = subGoal
    }
}
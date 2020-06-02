package be.hogent.faith.domain.models.goals

import java.util.UUID
import org.threeten.bp.LocalDateTime

const val MAX_AMOUNT_OF_SUBGOALS = 10
const val MAX_CHARACTERS_GOAL_DESCRIPTION = 60

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
        checkMaxLengthDescription(value)
        field = value
    }

    var chosenReachGoalWay: ReachGoalWay = ReachGoalWay.Elevator

    fun addSubGoalToGoal(newSubGoal: SubGoal, index : Int) {
        subGoals[index] = newSubGoal
    }

    fun removeSubGoalFromGoal(subGoal: SubGoal) {
        if (subGoals.contains(subGoal)){
            val subGoalsList = subGoals.toMutableList()
            subGoalsList.remove(subGoal)
            subGoals = subGoalsList.toTypedArray()
        }
        else
            throw IllegalArgumentException("Subdoel is niet aanwezig in de lijst")
    }

    //Verwisselen? of moet alles echt een positie opschuiven
    fun changeIndexSubGoal(subGoal: SubGoal, newIndex : Int){
        val temp = subGoals[subGoals.indexOf(subGoal)]
        subGoals[subGoals.indexOf(subGoal)] = subGoals[newIndex]
        subGoals[newIndex] = temp
    }

    internal fun setCompleted(){
        isCompleted = !isCompleted
    }

    fun moveAvatarToSubGoal(subGoal: SubGoal) {
        if(subGoals.contains(subGoal))
            currentPositionAvatar = subGoals.indexOf(subGoal)
    }
}

fun checkMaxLengthDescription(newDescription: String) {
    if (newDescription.length <= 30)
        throw IllegalArgumentException("Beschrijving mag niet groter zijn dan $MAX_CHARACTERS_GOAL_DESCRIPTION")
}
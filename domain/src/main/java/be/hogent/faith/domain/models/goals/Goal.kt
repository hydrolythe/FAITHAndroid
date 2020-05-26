package be.hogent.faith.domain.models.goals

import java.util.UUID
import org.threeten.bp.LocalDateTime

const val MAX_AMOUNT_OF_SUBGOALS = 10
const val MAX_CHARACTERS_GOAL_DESCRIPTION = 60

enum class ReachGoalWay {
    Stairs, Elevator, Rope
}

//Ik vond kleuren als naam gebruiken verwarrend.
enum class SkyscraperType{
    Skyscraper_1, Skyscraper_2, Skyscraper_3, Skyscraper_4, Skyscraper_5
}

class Goal(
    val uuid: UUID = UUID.randomUUID(),
    var description: String,
    val skyscraperType: SkyscraperType,
    var dateTime: LocalDateTime = LocalDateTime.now(),
    var isCompleted: Boolean = false,
    var currentPositionAvatar: Int = 0,
    var subGoals: Array<SubGoal?> = arrayOfNulls(MAX_AMOUNT_OF_SUBGOALS)
) {
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

    fun moveAvatarToSubGoal(subGoal: SubGoal) {
        if(subGoals.contains(subGoal))
            currentPositionAvatar = subGoals.indexOf(subGoal)
    }

    fun editDescription(newDescription: String) {
        if (newDescription.length <= 30)
            description = newDescription
        else
            throw IllegalArgumentException("Beschrijving mag niet groter zijn dan $MAX_CHARACTERS_GOAL_DESCRIPTION") }
}

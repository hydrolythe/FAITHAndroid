package be.hogent.faith.domain.models.goals

import java.util.UUID
import org.threeten.bp.LocalDateTime

const val MAX_AMOUNT_OF_SUBGOALS = 10

class Goal(
    val uuid: UUID = UUID.randomUUID(),
    var description : String,
    var dateTime: LocalDateTime = LocalDateTime.now()
){
    var isCompleted : Boolean = false

    private val _subGoals = mutableListOf<SubGoal>()
    val subGoals: List<SubGoal>
        get() = _subGoals

    fun addSubGoalToGoal(subGoal: SubGoal){
        if(!isCompleted){
            if(_subGoals.size >= MAX_AMOUNT_OF_SUBGOALS)
                throw IllegalArgumentException("Exceeded maximum amount of subgoals")
        }
        else
            throw IllegalArgumentException("Goal is completed")
    }

    fun removeSubGoalFromGoal(subGoal: SubGoal){
        if(!isCompleted){
            if(_subGoals.contains(subGoal))
                _subGoals.remove(subGoal)
            else
                throw IllegalArgumentException("Unable to find the selected SubGoal")
        }
        else
            throw IllegalArgumentException("Goal is completed")
    }

    fun setIsCompleted(){
        isCompleted = !isCompleted
    }
}


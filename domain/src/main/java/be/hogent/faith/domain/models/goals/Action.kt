package be.hogent.faith.domain.models.goals

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}

class Action(
    private var description : String = "",
    private var currentStatus : ActionStatus = ActionStatus.NEUTRAL
) {

    var currentPosition : Int = 0

    fun updateStatus(actionStatus: ActionStatus){
        currentStatus = actionStatus
    }

    fun editDescription(newDescription : String){
        if(newDescription.length <= 30)
            description = newDescription
        else
            throw IllegalArgumentException("Description > 30 characters")}

    fun changeCurrentPosition(newPosition : Int){ currentPosition = newPosition}

}
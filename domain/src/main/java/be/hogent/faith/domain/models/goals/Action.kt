package be.hogent.faith.domain.models.goals

class Action(
    private var description : String = "",
    private var currentStatus : ActionStatus = ActionStatus.NEUTRAL
) {

    fun editDescription(newDescription : String){
        description = newDescription
    }

    fun updateStatus(actionStatus: ActionStatus){
        currentStatus = actionStatus
    }
}

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}
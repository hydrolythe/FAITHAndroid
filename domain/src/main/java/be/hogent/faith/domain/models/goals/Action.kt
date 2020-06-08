package be.hogent.faith.domain.models.goals

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}
class Action(
    var currentStatus: ActionStatus = ActionStatus.NEUTRAL,
    var description: String = ""
)
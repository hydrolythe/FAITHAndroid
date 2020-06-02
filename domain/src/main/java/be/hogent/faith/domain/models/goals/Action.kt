package be.hogent.faith.domain.models.goals

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}

private const val MAX_CHARACTERS_ACTION_DESCRIPTION = 60
class Action(
    var currentStatus: ActionStatus = ActionStatus.NEUTRAL
) {
    var description: String = ""
        set(value) {
            require(value.length <= MAX_CHARACTERS_ACTION_DESCRIPTION) { "Beschrijving mag niet langer zijn dan $MAX_CHARACTERS_ACTION_DESCRIPTION tekens." }
            field = value
        }
}
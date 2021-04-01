package be.hogent.faith.faith.models.goals

import java.io.Serializable

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}

private const val MAX_CHARACTERS_ACTION_DESCRIPTION = 60

data class Action(
    var currentStatus: ActionStatus = ActionStatus.NEUTRAL
) : Serializable {
    var description: String = ""
        set(value) {
            require(value.length <= MAX_CHARACTERS_ACTION_DESCRIPTION) { "Beschrijving mag niet langer zijn dan $MAX_CHARACTERS_ACTION_DESCRIPTION tekens." }
            field = value
        }

    fun copy(): Action {
        return Action(this.currentStatus).also { it.description = this.description }
    }
}
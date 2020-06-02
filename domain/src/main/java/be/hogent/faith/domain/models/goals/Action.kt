package be.hogent.faith.domain.models.goals

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}

private const val MAX_CHARACTERS_ACTION_DESCRIPTION = 60
class Action(
    private var currentStatus: ActionStatus = ActionStatus.NEUTRAL
) {
    private var description: String = ""
        set(value) {
            require(value.length <= MAX_CHARACTERS_ACTION_DESCRIPTION) { "Beschrijving mag niet langer zijn dan $MAX_CHARACTERS_ACTION_DESCRIPTION tekens." }
            field = value
        }

    var currentPosition: Int? = -1

    fun editDescription(newDescription: String) {
        if (newDescription.length <= 30)
            description = newDescription
        else
            throw IllegalArgumentException("Description > 30 characters") }
}
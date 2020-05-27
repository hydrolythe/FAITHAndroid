package be.hogent.faith.domain.models.goals

enum class ActionStatus {
    NON_ACTIVE, NEUTRAL, ACTIVE
}

class Action(
    private var currentStatus: ActionStatus = ActionStatus.NEUTRAL
) {
    private var description: String = ""
        set(value) {
            checkMaxLengthDescription(value)
            field = value
        }

    var currentPosition: Int? = -1

    fun editDescription(newDescription: String) {
        if (newDescription.length <= 30)
            description = newDescription
        else
            throw IllegalArgumentException("Description > 30 characters") }
}
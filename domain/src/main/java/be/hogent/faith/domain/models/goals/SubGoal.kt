package be.hogent.faith.domain.models.goals

private const val DESCRIPTION_MAX_LENGTH = 30

class SubGoal(
    description: String
) {
    var description = description
        set(value) {
            require(value.length <= DESCRIPTION_MAX_LENGTH) { "Beschrijving mag niet langer dan $DESCRIPTION_MAX_LENGTH tekens zijn." }
            field = value
        }

    private val _actions = mutableListOf<Action>()
    val actions: List<Action>
        get() = _actions

    fun addAction(newAction: Action) {
        _actions.add(newAction)
    }

    fun removeAction(action: Action) {
        require(_actions.contains(action)) { "Deze actie hoort niet bij dit subdoel" }
        _actions.remove(action)
    }

    fun updateActionPosition(actionToUpdate: Action, newPosition: Int) {
        _actions.remove(actionToUpdate)
        _actions.add(newPosition, actionToUpdate)
    }
}
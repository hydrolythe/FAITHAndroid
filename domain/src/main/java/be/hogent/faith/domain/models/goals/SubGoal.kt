package be.hogent.faith.domain.models.goals

class SubGoal(
    private var description: String
) {

    private val _actions = mutableListOf<Action>()
    val actions: List<Action>
        get() = _actions

    fun addActionToSubGoal(newAction: Action) {
        _actions.add(newAction)
    }

    fun removeActionFromSubGoal(action: Action) {
        require(_actions.contains(action)) { "Deze actie hoort niet bij dit subdoel" }
        _actions.remove(action)
    }

    fun updateActionPosition(actionToUpdate: Action, newPosition: Int) {
        _actions.remove(actionToUpdate)
        _actions.add(newPosition, actionToUpdate)
    }

    fun editDescription(newDescription: String) {
        require(newDescription.length <= 30) { "Description > 30 characters" }
        description = newDescription
    }
}
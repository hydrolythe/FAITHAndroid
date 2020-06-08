package be.hogent.faith.domain.models.goals

class SubGoal(
    var description: String
) {
    private val _actions = mutableListOf<Action>()
    val actions: List<Action>
        get() = _actions

    fun addAction(newAction: Action) {
        _actions.add(newAction)
    }
}

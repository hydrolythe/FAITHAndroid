package be.hogent.faith.domain

class ActionPlan {

    private val _goals = mutableListOf<Goal>()
    val goals: List<Goal>
        get() = _goals

    fun addGoal(goal: Goal) {
        _goals += goal
    }
}
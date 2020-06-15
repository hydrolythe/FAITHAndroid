package be.hogent.faith.domain.models

import be.hogent.faith.domain.models.goals.Goal
import be.hogent.faith.domain.models.goals.GoalColor
import java.util.UUID

const val MAX_NUMBER_OF_ACTIVE_GOALS = 5

data class User(
    val username: String,
    /**
     * The name of the avatar this user chose.
     * This should be unique, and will be used to request the image corresponding to the chosen avatar.
     */
    var avatarName: String,
    // generated by Firebase, and they don't generate valid UUID's
    val uuid: String
) {
    private var _events = HashMap<UUID, Event>()
    val events: List<Event>
        get() = _events.values.toList()

    private var allGoals = mutableListOf<Goal>()

    val achievedGoals: List<Goal>
        get() = allGoals.filter { it.isCompleted }

    val activeGoals: List<Goal>
        get() = allGoals.filter { it.isCompleted.not() }

    val backpack = Backpack()

    val cinema = Cinema()

    val treasureChest = TreasureChest()

    fun addEvent(event: Event) {
        if (event.title.isNullOrBlank()) {
            throw IllegalArgumentException("Een gebeurtenis moet een ingevulde titel hebben.")
        }
        _events[event.uuid] = event
    }

    fun getEvent(eventUUID: UUID): Event? {
        return _events[eventUUID]
    }

    fun clearEvents() {
        _events = HashMap()
    }

    fun removeEvent(event: Event) {
        _events.remove(event.uuid)
    }

    fun addGoal() {
        require(activeGoals.size < MAX_NUMBER_OF_ACTIVE_GOALS) { "Er kunnen maximum $MAX_NUMBER_OF_ACTIVE_GOALS actieve doelen zijn." }
        val uniqueColor = findUnusedColor()
        val goal = Goal(uniqueColor)
        allGoals.add(goal)
    }

    fun removeGoal(goal: Goal) {
        allGoals.remove(goal)
    }
    /**
     * Uses the list of [GoalColor]s to find a color that is not currently used in the [activeGoals].
     */
    private fun findUnusedColor(): GoalColor {
        val currentlyUsedColors = activeGoals.map { it.goalColor }
        val availableColors = GoalColor.values().subtract(currentlyUsedColors)
        return availableColors.first()
    }

    fun setGoalCompleted(goal: Goal) {
        goal.toggleCompleted()
    }

    fun setGoals(goals: List<Goal>) {
        allGoals = goals.toMutableList()
    }
}

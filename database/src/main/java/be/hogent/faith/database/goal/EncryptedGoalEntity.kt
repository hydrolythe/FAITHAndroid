package be.hogent.faith.database.goal

data class EncryptedGoalEntity(
    val dateTime: String = "",
    val description: String = "",
    val uuid: String = "",
    val completed: String = "",
    val currentPositionAvatar: Int = 1,
    val color: String = "",
    val reachGoalWay: String = "",
    var subgoals: List<EncryptedSubGoalEntity> = emptyList(),
    val encryptedDEK: String = ""
)

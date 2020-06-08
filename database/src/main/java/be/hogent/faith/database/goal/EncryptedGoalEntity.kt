package be.hogent.faith.database.goal

import java.util.UUID

data class EncryptedGoalEntity(
    val dateTime: String = "",
    val description: String = "",
    val uuid: String = UUID.randomUUID().toString(),
    val isCompleted: Boolean = true,
    val currentPositionAvatar: Int = 1,
    val color: String = "",
    val reachGoalWay: String = "",
    var subgoals: List<EncryptedSubGoalEntity> = emptyList(),
    val encryptedDEK: String = ""
)

package be.hogent.faith.database.goal

data class EncryptedSubGoalEntity(
    val sequenceNumber: Int = 1,
    val description: String = "",
    var actions: List<EncryptedActionEntity> = emptyList()
)
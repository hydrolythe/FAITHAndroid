package be.hogent.faith.service.encryption

class EncryptedSubGoal(
    val sequenceNumber: Int,
    val description: EncryptedString = "",
    var actions: List<EncryptedAction> = emptyList()
)
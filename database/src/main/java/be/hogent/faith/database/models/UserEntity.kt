package be.hogent.faith.database.models

// Firestore needs a default constructor, so a default value for all parameters
data class UserEntity(
    val uuid: String = "",
    val username: String = "",
    val avatarName: String = ""
)
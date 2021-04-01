package be.hogent.faith.faith

import be.hogent.faith.faith.models.User

data class TokenResult(val success:Token?=null,val exception:Exception?=null)

data class UserResult(val success: User?=null, val exception:Exception?=null)

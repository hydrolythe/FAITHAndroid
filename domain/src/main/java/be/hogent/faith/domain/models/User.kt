package be.hogent.faith.domain.models

class User(
    val eventLog: EventLog = EventLog(),

    /**
     * The [Avatar] used to represent the user.
     */
    val avatar : Avatar,

    /**
     * User name of the user.
     */
    val username : String




)

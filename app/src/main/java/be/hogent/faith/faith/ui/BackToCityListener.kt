package be.hogent.faith.faith.ui

interface BackToCityListener {
    fun needConfirmation(): Boolean
    fun getConfirmationTitle(): String
    fun getConfirmationMessage():String
}
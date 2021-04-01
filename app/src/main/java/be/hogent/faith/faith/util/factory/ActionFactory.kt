package be.hogent.faith.faith.util.factory

import be.hogent.faith.faith.models.goals.Action
import be.hogent.faith.faith.models.goals.ActionStatus

object ActionFactory {

    fun makeAction(): Action {
        return Action(ActionStatus.ACTIVE).also { it.description = DataFactory.randomString() }
    }
}
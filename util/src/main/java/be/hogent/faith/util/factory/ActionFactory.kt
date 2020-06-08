package be.hogent.faith.util.factory

import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.ActionStatus

object ActionFactory {

    fun makeAction(): Action {
        return Action(ActionStatus.ACTIVE).also { it.description = DataFactory.randomString()}
    }
}
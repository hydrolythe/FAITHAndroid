package be.hogent.faith.domain

import be.hogent.faith.domain.models.goals.Action
import be.hogent.faith.domain.models.goals.SubGoal
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SubGoalTest {
    private lateinit var subGoal: SubGoal

    private var action1 = Action()
    private var action2 = Action()
    private var action3 = Action()
    private var action4 = Action()
    private var action5 = Action()
    private var action6 = Action()
    private var action7 = Action()
    private var action8 = Action()
    private var action9 = Action()
    private var action10 = Action()

    @Before
    fun setUp() {
        subGoal = SubGoal("")
    }

    @Test
    fun `change index action`() {
        subGoal.addAction(action1)
        subGoal.addAction(action2)
        subGoal.addAction(action3)
        subGoal.addAction(action4)

        subGoal.updateActionPosition(action2, 2)

        Assert.assertEquals(subGoal.actions.elementAt(0), action1)
        Assert.assertEquals(subGoal.actions.elementAt(1), action3)
        Assert.assertEquals(subGoal.actions.elementAt(2), action2)
        Assert.assertEquals(subGoal.actions.elementAt(3), action4)
    }

    // action 3 naar plaats van action 8 : 1 - 2 - 3 - 4 - 5 - 6 - 7 - 8 - 9 - 10 --> 1 - 2 - 4 - 5 - 6 - 7 - 8 - 3 - 9- 10
    @Test
    fun `list 10 actions test`() {
        subGoal.addAction(action1)
        subGoal.addAction(action2)
        subGoal.addAction(action3)
        subGoal.addAction(action4)
        subGoal.addAction(action5)
        subGoal.addAction(action6)
        subGoal.addAction(action7)
        subGoal.addAction(action8)
        subGoal.addAction(action9)
        subGoal.addAction(action10)

        subGoal.updateActionPosition(action3, 7)

        Assert.assertEquals(subGoal.actions.elementAt(0), action1)
        Assert.assertEquals(subGoal.actions.elementAt(1), action2)
        Assert.assertEquals(subGoal.actions.elementAt(2), action4)
        Assert.assertEquals(subGoal.actions.elementAt(3), action5)
        Assert.assertEquals(subGoal.actions.elementAt(4), action6)
        Assert.assertEquals(subGoal.actions.elementAt(5), action7)
        Assert.assertEquals(subGoal.actions.elementAt(6), action8)
        Assert.assertEquals(subGoal.actions.elementAt(7), action3)
        Assert.assertEquals(subGoal.actions.elementAt(8), action9)
        Assert.assertEquals(subGoal.actions.elementAt(9), action10)
    }
}
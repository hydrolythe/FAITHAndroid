package be.hogent.faith.domain

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EmotionTest {

    private lateinit var emotion: Emotion

    @Before
    fun setUp() {
        emotion = Emotion(mockk())
    }

    @Test
    fun `Emotion constructor starts with empty list of details`() {
        assert(emotion.details.isEmpty())
    }

    @Test
    fun `Emotion addDetail correctly adds the detail`() {
        val detail = mockk<Detail>()

        emotion.addDetail(detail)

        assertEquals(1, emotion.details.size)
        assertEquals(detail, emotion.details.first())
    }

    @Test
    fun `Emotion getDetails returns all details`() {
        for (i in 1..10) {
            emotion.addDetail(mockk())
        }

        assertEquals(10, emotion.details.size)
    }
}
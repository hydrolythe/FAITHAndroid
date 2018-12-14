package be.hogent.faith.domain

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EmotionDiaryTest {

    private lateinit var diary: EmotionDiary

    @Before
    fun setUp() {
        diary = EmotionDiary()
    }

    @Test
    fun `EmotionDiary constructor starts with empty list of emotions`() {
        assert(diary.emotions.isEmpty())
    }

    @Test
    fun `EmotionDiary addEmotion correctly adds the emotion`() {
        val emotion = mockk<Emotion>()

        diary.addEmotion(emotion)

        assertEquals(1, diary.emotions.size)
        assertEquals(emotion, diary.emotions.first())
    }

    @Test
    fun `EmotionDiary getGoals returns all goals`() {
        for (i in 1..10) {
            diary.addEmotion(mockk())
        }

        assertEquals(10, diary.emotions.size)
    }
}
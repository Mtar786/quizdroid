package edu.uw.ischool.mrahma3.quizdroid


import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TopicRepositoryUnitTest {

    // Create a mock implementation of the TopicRepository for testing
    private val topicRepository = object : TopicRepository {
        override fun getTopics(): List<Topic> {
            return listOf(
                Topic(
                    "Math",
                    "Math Overview",
                    "Explore the world of numbers, quantity, and space.",
                    listOf(
                        Question("What is 2 + 2?", listOf("3", "4", "5", "6"), 1),
                        Question("What is the square root of 16?", listOf("2", "4", "8", "16"), 1)
                    )
                )
            )
        }

        override fun getTopicById(topicId: String): Topic? {
            return getTopics().find { it.title == topicId }
        }
    }

    @Test
    fun testGetTopics() {
        val topics = topicRepository.getTopics()
        assertNotNull(topics)
        assertEquals(1, topics.size)
        assertEquals("Math", topics[0].title)
    }

    @Test
    fun testGetTopicById() {
        val topic = topicRepository.getTopicById("Math")
        assertNotNull(topic)
        assertEquals("Math", topic?.title)
    }

    @Test
    fun testGetTopicById_NonExistent() {
        val topic = topicRepository.getTopicById("NonExistent")
        assertNull(topic)
    }
}

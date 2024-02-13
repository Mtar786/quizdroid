package edu.uw.ischool.mrahma3.quizdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider

    data class Question(
        val questionText: String,
        val answers: List<String>,
        val correctAnswerIndex: Int
    )

    data class Topic(
        val title: String,
        val shortDescription: String,
        val longDescription: String,
        val iconResId: Int,
        val questions: List<Question>
    )

    interface TopicRepository {
        fun getTopics(): List<Topic>
        fun getTopicById(topicId: String): Topic?
    }

    class InMemoryTopicRepository : TopicRepository {
        private val topics: List<Topic> = listOf(
            Topic(
                "Mathematics",
                "Mathematics Overview",
                "Explore the world of numbers, quantity, and space.",
                R.drawable.ic_launcher_foreground, // Use a stock Android icon for mathematics
                listOf(
                    Question("What is 2 + 2?", listOf("3", "4", "5", "6"), 1),
                    Question("What is the square root of 16?", listOf("2", "4", "8", "16"), 1)
                )
            ),
            Topic(
                "Physics",
                "Physics Overview",
                "Unravel the principles governing matter and energy.",
                R.drawable.ic_launcher_foreground, // Use a stock Android icon for physics
                listOf(
                    Question("What is Newton's first law of motion?", listOf("An object in motion stays in motion", "F = ma", "Every action has an equal and opposite reaction", "Objects fall at the same rate regardless of mass"), 0),
                    Question("What is the SI unit of force?", listOf("Newton", "Watt", "Joule", "Pascal"), 0)
                )
            ),
            Topic(
                "Marvel Super Heroes",
                "Marvel Super Heroes Overview",
                "Dive into the realm of iconic superheroes.",
                R.drawable.ic_launcher_foreground, // Use a stock Android icon for Marvel Super Heroes
                listOf(
                    Question("Who is Iron Man's alter ego?", listOf("Tony Stark", "Steve Rogers", "Peter Parker", "Bruce Wayne"), 0),
                    Question("What is the name of Thor's hammer?", listOf("Stormbreaker", "Mjolnir", "Gungnir", "Excalibur"), 1)
                )
            )
        )

        override fun getTopics(): List<Topic> {
            return topics
        }

        override fun getTopicById(topicId: String): Topic? {
            return topics.find { it.title == topicId }
        }
    }

class MainActivity : AppCompatActivity() {

    private lateinit var quizViewModel: QuizViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, TopicListFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Check if the current fragment is an instance of AnswerFragment or QuestionFragment
        if (fragment is QuestionFragment) {
            quizViewModel.resetCorrectCount()
            handleQuestionBackNavigation(fragment)
        } else {
            // If it's neither AnswerFragment nor QuestionFragment, let the system handle back navigation
            super.onBackPressed()
        }
    }

    private fun handleQuestionBackNavigation(fragment: Fragment) {
        // Check if it's a QuestionFragment
        if (fragment is QuestionFragment) {
            val args = fragment.arguments
            val questionIndex = args?.getInt("questionIndex", 0)
            if (questionIndex == 0) {
                // If it's the first question page, go back to the topic list page
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                return
            } else {
                supportFragmentManager.popBackStack()
                supportFragmentManager.popBackStack()
            }
        }
    }



}
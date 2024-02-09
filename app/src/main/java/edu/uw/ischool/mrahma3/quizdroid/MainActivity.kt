package edu.uw.ischool.mrahma3.quizdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private val topics = listOf("Math, Physics, Marvel Super Heroes")
    lateinit var items: RecyclerView
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
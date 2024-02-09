package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AnswerFragment : Fragment() {

    private var selectedAnswer: String? = null
    private var correctAnswer: String? = null
    private var totalQuestions: Int = 0
    private var correctCount: Int = 0
    private var questionIndex: Int = 0
    private var topic: String? = null
    private lateinit var quizViewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_answer, container, false)

        // Retrieve answer information from arguments
        arguments?.let { args ->
            selectedAnswer = args.getString("selectedAnswer")
            correctAnswer = args.getString("correctAnswer")
            totalQuestions = args.getInt("totalQuestions", 0)
            questionIndex = args.getInt("questionIndex", 0)
            topic = args.getString("topic")
            correctCount = quizViewModel.correctCount
        }


        // Display selected and correct answers
        view.findViewById<TextView>(R.id.selectedAnswerTextView).text = "Your answer: $selectedAnswer"
        view.findViewById<TextView>(R.id.correctAnswerTextView).text = "Correct answer: $correctAnswer"

        // Update correct count
        if (selectedAnswer == correctAnswer) {
            quizViewModel.correctCount++
        }

        // Display total number of correct vs incorrect answers
        val resultText = "You have ${quizViewModel.correctCount} out of $totalQuestions correct"
        view.findViewById<TextView>(R.id.resultTextView).text = resultText

        // Display "Next" button or "Finish" button based on whether there are more questions remaining
        val nextButton = view.findViewById<Button>(R.id.nextButton)
        val finishButton = view.findViewById<Button>(R.id.finishButton)
        if (questionIndex < totalQuestions - 1) {
            nextButton.visibility = View.VISIBLE
            nextButton.setOnClickListener {
                // Navigate to the next question page
                val nextQuestionIndex = questionIndex + 1
                val nextQuestionFragment = QuestionFragment.newInstance(topic!!, nextQuestionIndex)
                navigateToNextQuestion(nextQuestionFragment)
            }

        } else {
            finishButton.visibility = View.VISIBLE
            finishButton.text = "Finish"
            quizViewModel.correctCount = 0
            finishButton.setOnClickListener {
                // Navigate back to the first topic list page
                requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        return view
    }

    private fun navigateToNextQuestion(nextQuestionFragment: QuestionFragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextQuestionFragment)
            .addToBackStack(null)
            .commit()
    }
}

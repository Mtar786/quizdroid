package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class QuestionFragment : Fragment() {

    private lateinit var topic: String
    private var questionIndex: Int = 0
    private lateinit var topicRepository: TopicRepository
    private lateinit var currentTopic: Topic
    private lateinit var currentQuestion: Question

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)
        topicRepository = (requireActivity().application as QuizApp).topicRepository

        retrieveArguments()

        currentTopic = topicRepository.getTopicById(topic) ?: Topic("", "", "", R.drawable.ic_launcher_foreground, emptyList())
        currentQuestion = currentTopic.questions.getOrElse(questionIndex) { Question("", emptyList(), 0) }

        setupQuestion(view)
        setupSubmitButton(view)

        return view
    }

    private fun retrieveArguments() {
        topic = requireArguments().getString("topic") ?: ""
        questionIndex = requireArguments().getInt("questionIndex", 0)
    }

    private fun setupQuestion(view: View) {
        view.findViewById<TextView>(R.id.questionTextView).text = currentQuestion.questionText
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.removeAllViews() // Clear existing views

        currentQuestion.answers.forEachIndexed { index, answer ->
            val radioButton = RadioButton(requireContext())
            radioButton.text = answer
            radioButton.id = View.generateViewId() // Generate a unique ID
            radioGroup.addView(radioButton)
        }
    }

    private fun setupSubmitButton(view: View) {
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        submitButton.visibility = View.GONE // Initially hide the submit button
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            submitButton.visibility = if (checkedId != -1) View.VISIBLE else View.GONE
        }

        submitButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
                val selectedAnswer = selectedRadioButton.text.toString()
                val correctAnswer = currentQuestion.answers[currentQuestion.correctAnswerIndex]

                val bundle = Bundle().apply {
                    putString("selectedAnswer", selectedAnswer)
                    putString("correctAnswer", correctAnswer)
                    putString("topic", topic)
                    putInt("totalQuestions", currentTopic.questions.size)
                    putInt("questionIndex", questionIndex)
                }

                val fragment = AnswerFragment().apply {
                    arguments = bundle
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Log.w("QuestionFragment", "No option selected")
            }
        }
    }

    companion object {
        fun newInstance(topic: String, questionIndex: Int): QuestionFragment {
            val fragment = QuestionFragment()
            val bundle = Bundle().apply {
                putString("topic", topic)
                putInt("questionIndex", questionIndex)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}
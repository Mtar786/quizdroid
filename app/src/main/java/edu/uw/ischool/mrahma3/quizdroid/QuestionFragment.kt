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
import androidx.lifecycle.ViewModelProvider

class QuestionFragment : Fragment() {

    private var questionIndex: Int = 0
    private var topic: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)

        // Retrieve the selected topic from arguments
        val topic = arguments?.getString("topic")
        this.topic = topic

        // Retrieve the question index from arguments
        questionIndex = arguments?.getInt("questionIndex", 0) ?: 0

        // Fetch questions and options based on the selected topic
        val (questionText, options, correctAnswerIndex) = fetchQuestionsAndOptions(topic)
        val correctAnswer = options[correctAnswerIndex]

        // Update views with question information
        view.findViewById<TextView>(R.id.questionTextView).text = questionText
        view.findViewById<RadioButton>(R.id.option1RadioButton).text = options[0]
        view.findViewById<RadioButton>(R.id.option2RadioButton).text = options[1]
        view.findViewById<RadioButton>(R.id.option3RadioButton).text = options[2]
        view.findViewById<RadioButton>(R.id.option4RadioButton).text = options[3]

        // Set up "Submit" button click listener
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        submitButton.visibility = View.GONE // Initially hide the submit button
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            submitButton.visibility = if (checkedId != -1) View.VISIBLE else View.GONE
        }

        submitButton.setOnClickListener {
            // Check if a radio button is selected
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                // A radio button is selected, navigate to the answer page
                val selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
                val selectedAnswer = selectedRadioButton.text.toString()

                val bundle = Bundle().apply {
                    putString("selectedAnswer", selectedAnswer)
                    putString("correctAnswer", correctAnswer)
                    putString("topic", topic)
                    putInt("totalQuestions", 2)
                    putInt("questionIndex", questionIndex)
                }
                Log.i("QuestionFragment", "This is the bundle:" + bundle)

                val fragment = AnswerFragment()
                fragment.arguments = bundle


                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                Log.w("QuestionFragment", "Check the question fragment. Something went wrong.")
            }
        }


        return view
    }


    private fun fetchQuestionsAndOptions(topic: String?): Triple<String, Array<String>, Int> {
        val questions = when (topic) {
            "Math" -> getMathQuestions()
            "Physics" -> getPhysicsQuestions()
            "Marvel Super Heroes" -> getMarvelQuestions()
            else -> emptyList()
        }

        return if (questions.isNotEmpty()) {
            val question = questions[questionIndex]
            val questionText = question.first
            val options = question.second.toTypedArray()
            val correctAnswerIndex = question.third
            Triple(questionText, options, correctAnswerIndex)
        } else {
            // Handle the case when no questions are available for the topic
            Log.d("QuestionFragment", "No questions available for $topic")
            // You can display a message or take appropriate action here
            Triple("No question available", arrayOf(), -1)
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

    private fun getMathQuestions(): List<Triple<String, List<String>, Int>> {
        return listOf(
            Triple("What is 2 + 2?", listOf("3", "4", "5", "6"), 1), // Index of correct answer
            Triple("What is the square root of 16?", listOf("2", "4", "8", "16"), 1)
        )
    }

    private fun getPhysicsQuestions(): List<Triple<String, List<String>, Int>> {
        return listOf(
            Triple("What is Newton's first law of motion?", listOf("An object in motion stays in motion", "F = ma", "Every action has an equal and opposite reaction", "Objects fall at the same rate regardless of mass"), 0), // Index of correct answer
            Triple("What is the SI unit of force?", listOf("Newton", "Watt", "Joule", "Pascal"), 0)
        )
    }

    private fun getMarvelQuestions(): List<Triple<String, List<String>, Int>> {
        return listOf(
            Triple("Who is Iron Man's alter ego?", listOf("Tony Stark", "Steve Rogers", "Peter Parker", "Bruce Wayne"), 0), // Index of correct answer
            Triple("What is the name of Thor's hammer?", listOf("Stormbreaker", "Mjolnir", "Gungnir", "Excalibur"), 1)
        )
    }
}



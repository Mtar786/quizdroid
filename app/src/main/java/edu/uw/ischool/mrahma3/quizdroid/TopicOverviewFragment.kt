package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class TopicOverviewFragment : Fragment() {

    private lateinit var topic: Topic
    private var totalQuestions: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic_overview, container, false)

        retrieveArguments()
        setupTopicDescription(view)
        setupBeginButton(view)

        return view
    }

    private fun retrieveArguments() {
        val topicId = requireArguments().getString("topic") ?: ""
        topic = (requireActivity().applicationContext as QuizApp).topicRepository.getTopicById(topicId) ?: Topic("", "", "", R.drawable.ic_launcher_foreground, emptyList())
        totalQuestions = requireArguments().getInt("totalQuestions", 0)
    }

    private fun setupTopicDescription(view: View) {
        view.findViewById<TextView>(R.id.topicDescriptionTextView).text = topic.longDescription
        view.findViewById<TextView>(R.id.totalQuestionsTextView).text = "Total Questions: $totalQuestions"

        // Set the icon in the ImageView
        view.findViewById<ImageView>(R.id.iconImageView).setImageResource(topic.iconResId)
    }

    private fun setupBeginButton(view: View) {
        view.findViewById<Button>(R.id.beginButton).setOnClickListener {
            navigateToQuestionFragment()
        }
    }

    private fun navigateToQuestionFragment() {
        val bundle = Bundle().apply {
            putString("topic", topic.title)
        }
        val fragment = QuestionFragment().apply {
            arguments = bundle
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}






package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import android.util.Log
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
    private lateinit var topicRepository: TopicRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic_overview, container, false)

        // Initialize the topicRepository with JsonTopicRepository
        topicRepository = JsonTopicRepository(requireContext())

        retrieveArguments()
        setupTopicDescription(view)
        setupBeginButton(view)

        return view
    }

    private fun retrieveArguments() {
        val topicId = requireArguments().getString("topic") ?: ""
        topic = topicRepository.getTopicById(topicId) ?: Topic("", "",  emptyList())
        totalQuestions = requireArguments().getInt("totalQuestions", 0)
    }

    private fun setupTopicDescription(view: View) {
        Log.i("message", "here is the topic: " + topic.desc)
        view.findViewById<TextView>(R.id.topicDescriptionTextView).text = topic.desc
        view.findViewById<TextView>(R.id.totalQuestionsTextView).text = "Total Questions: $totalQuestions"
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






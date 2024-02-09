package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.io.Serializable

class TopicOverviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic_overview, container, false)

        val topic = arguments?.getString("topic")
        val totalQuestions = arguments?.getInt("totalQuestions") ?: 0
        val topicDescription = when (topic) {
            "Math" -> "Math Overview: Explore the world of numbers, quantity, and space"
            "Physics" -> "Phyaics Overview: Unravel the principles governing matter and energy."
            "Marvel Super Heroes" -> "Marvel Super Heroes Overview: Dive into the realm of iconic superheroes."
            else -> "Description not available."
        }

        view.findViewById<TextView>(R.id.topicDescriptionTextView).text = topicDescription
        view.findViewById<TextView>(R.id.totalQuestionsTextView).text = "Total Questions: $totalQuestions"

        view.findViewById<Button>(R.id.beginButton).setOnClickListener {
            // Navigate to QuestionFragment passing the topic
            val bundle = Bundle()
            bundle.putString("topic", topic)
            val fragment = QuestionFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}



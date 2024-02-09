package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TopicListFragment : Fragment() {

    private val topics = listOf("Math", "Physics", "Marvel Super Heroes")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TopicAdapter(topics) { selectedTopic ->
            // Handle topic item click event
            val totalQuestions = 2 // Replace with the actual total number of questions for the selected topic
            navigateToTopicOverview(selectedTopic)
        }
        recyclerView.adapter = adapter

        return view
    }

    private fun navigateToTopicOverview(selectedTopic: String) {
        val bundle = Bundle()
        bundle.putString("topic", selectedTopic)
        bundle.putInt("totalQuestions", 2)
        val fragment = TopicOverviewFragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}



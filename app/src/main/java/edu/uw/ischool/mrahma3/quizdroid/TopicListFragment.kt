package edu.uw.ischool.mrahma3.quizdroid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TopicListFragment : Fragment() {

    private val topicRepository: TopicRepository by lazy {
        (requireActivity().applicationContext as QuizApp).topicRepository
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_topic_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val topics = topicRepository.getTopics()
        val adapter = TopicAdapter(topics) { selectedTopic ->
            // Get the total number of questions for the selected topic
            val totalQuestions = topicRepository.getTopicById(selectedTopic)?.questions?.size ?: 0
            navigateToTopicOverview(selectedTopic, totalQuestions)
        }
        recyclerView.adapter = adapter

        return view
    }

    private fun navigateToTopicOverview(selectedTopic: String, totalQuestions: Int) {
        val bundle = Bundle().apply {
            putString("topic", selectedTopic)
            putInt("totalQuestions", totalQuestions)
        }
        val fragment = TopicOverviewFragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}






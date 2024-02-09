package edu.uw.ischool.mrahma3.quizdroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TopicAdapter(private val topics: List<String>, private val onItemClick: (String) -> Unit)
    : RecyclerView.Adapter<TopicAdapter.TopicViewHolder>() {

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicTextView: TextView = itemView.findViewById(R.id.topicTextView)

        fun bind(topic: String) {
            topicTextView.text = topic
            itemView.setOnClickListener { onItemClick(topic) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bind(topics[position])
    }

    override fun getItemCount(): Int {
        return topics.size
    }
}

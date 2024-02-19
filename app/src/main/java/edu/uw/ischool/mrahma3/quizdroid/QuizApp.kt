package edu.uw.ischool.mrahma3.quizdroid

import android.app.Application
import android.util.Log

class QuizApp : Application() {
    val topicRepository: TopicRepository by lazy { JsonTopicRepository(this) }

    override fun onCreate() {
        super.onCreate()
        Log.d("QuizApp", "QuizApp is being loaded and run")
    }
}

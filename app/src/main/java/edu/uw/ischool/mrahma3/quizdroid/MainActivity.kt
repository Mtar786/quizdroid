package edu.uw.ischool.mrahma3.quizdroid

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import java.io.IOException

data class Question(
        val text: String,
        val answers: List<String>,
        val answer: Int
    )

    data class Topic(
        val title: String,
        val desc: String,
        val questions: List<Question>
    )

    interface TopicRepository {
        fun getTopics(): List<Topic>
        fun getTopicById(topicId: String): Topic?
    }

class JsonTopicRepository(private val context: Context) : TopicRepository {

    override fun getTopics(): List<Topic> {
        val topicsJson = context.assets.open("questions.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(topicsJson, Array<Topic>::class.java).toList()
    }

    override fun getTopicById(topicId: String): Topic? {
        return getTopics().find { it.title == topicId }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var quizViewModel: QuizViewModel
    private lateinit var preferences: SharedPreferences
    private var isDownloadInProgress: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

        // Inflate the menu programmatically
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set onClickListener to the toolbar home button
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, Preferences::class.java))
                    true
                }
                else -> false
            }
        }

        // Add the preferences item to the action bar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_launcher_background)
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val url = preferences.getString("pref_key_url", "")
        val downloadIntervalString = preferences.getString("pref_key_refresh_interval", DEFAULT_INTERVAL.toString())
        val downloadInterval = downloadIntervalString?.toIntOrNull() ?: DEFAULT_INTERVAL


        // Use the retrieved values as needed
        // For example, log them to check if they are stored correctly
        Log.d("MainActivity", "Question Data URL: $url")
        Log.d("MainActivity", "Refresh Interval: $downloadInterval")

        if (isDownloadInProgress) {
            // Disable the settings or indicate to the user that they can't be changed now
            disableSettings()
        } else {
            // Apply settings as usual
            applySettings()
        }

        val topicRepository = JsonTopicRepository(this)
        val topics = topicRepository.getTopics()

        // Log the list of topics
        Log.d("MainActivity", "Topics: $topics")

        // Loop through the topics and log specific properties
        topics.forEach { topic ->
            Log.d("MainActivity", "Title: ${topic.title}")
            Log.d("MainActivity", "Questions: ${topic.questions}")
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TopicListFragment())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the preferences action here
                startActivity(Intent(this, Preferences::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun applySettings() {
        // Retrieve settings from SharedPreferences
        val url = preferences.getString("pref_key_url", DEFAULT_URL)
        val downloadInterval = preferences.getString("pref_key_refresh_interval", DEFAULT_INTERVAL.toString())

        // Apply settings within the app
        // For example, update URL to use for question data

        // Handle download logic based on download status
        // For example, prevent settings from taking effect until the next download starts if a download is underway
        Log.i("MainActivity", "Settings applied - URL: $url, Download Interval: $downloadInterval minutes")
    }

    // Method to disable settings when a download is in progress
    private fun disableSettings() {
        // Disable the settings UI elements or show a message indicating that settings can't be changed now
        Log.i("MainActivity", "Settings disabled due to download in progress.")
    }

    // Method to handle the download completion
    private fun onDownloadComplete() {
        isDownloadInProgress = false
        // Apply settings when the download completes
        applySettings()
        Log.i("MainActivity", "Download complete.")
    }

    // Method to start the download
    private fun startDownload() {
        isDownloadInProgress = true
        val downloadInterval = preferences.getInt("interval_key", DEFAULT_INTERVAL)
        // Start the download process here with the specified interval
        // Once the download is complete, call onDownloadComplete()
        Log.i("MainActivity", "Download started with interval: $downloadInterval minutes")
    }

    companion object {
        private const val DEFAULT_URL = "default_url"
        private const val DEFAULT_INTERVAL = 60 // Default interval in minutes
    }




    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // Check if the current fragment is an instance of AnswerFragment or QuestionFragment
        if (fragment is QuestionFragment) {
            quizViewModel.resetCorrectCount()
            handleQuestionBackNavigation(fragment)
        } else {
            // If it's neither AnswerFragment nor QuestionFragment, let the system handle back navigation
            super.onBackPressed()
        }
    }

    private fun handleQuestionBackNavigation(fragment: Fragment) {
        // Check if it's a QuestionFragment
        if (fragment is QuestionFragment) {
            val args = fragment.arguments
            val questionIndex = args?.getInt("questionIndex", 0)
            if (questionIndex == 0) {
                // If it's the first question page, go back to the topic list page
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                return
            } else {
                supportFragmentManager.popBackStack()
                supportFragmentManager.popBackStack()
            }
        }
    }

}
package edu.uw.ischool.mrahma3.quizdroid

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

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
        return loadDownloadedTopics() ?: loadDefaultTopicsFromAssets()
    }

    private fun loadDownloadedTopics(): List<Topic>? {
        val downloadedFile = File(context.filesDir, "questions.json")
        return try {
            if (downloadedFile.exists() && downloadedFile.length() > 0) {
                val topicsJson = downloadedFile.bufferedReader().use { it.readText() }
                Gson().fromJson(topicsJson, Array<Topic>::class.java).toList()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("JsonTopicRepository", "Error parsing downloaded topics", e)
            null
        }
    }

    private fun loadDefaultTopicsFromAssets(): List<Topic> {
        return try {
            val topicsJson = context.assets.open("questions.json").bufferedReader().use { it.readText() }
            Gson().fromJson(topicsJson, Array<Topic>::class.java).toList()
        } catch (e: Exception) {
            Log.e("JsonTopicRepository", "Error loading default topics from assets", e)
            emptyList()
        }
    }

    override fun getTopicById(topicId: String): Topic? {
        return getTopics().find { it.title == topicId }
    }
}


class MainActivity : AppCompatActivity() {

    private lateinit var quizViewModel: QuizViewModel
    private lateinit var preferences: SharedPreferences
    private val executor = Executors.newFixedThreadPool(5)

    private lateinit var progressDialog: ProgressDialog

    private fun showProgressDialog() {
        runOnUiThread {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Downloading questions...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }
    }


    private fun dismissProgressDialog() {
        // Add a delay of 1 second (adjust as needed)
        Handler(Looper.getMainLooper()).postDelayed({
            if (::progressDialog.isInitialized && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
        }, 1000)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        quizViewModel = ViewModelProvider(this).get(QuizViewModel::class.java)

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

        checkNetworkStatus()
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val url = preferences.getString("pref_key_url", "")
        val downloadIntervalString = preferences.getString("pref_key_refresh_interval", DEFAULT_INTERVAL.toString())
        val downloadInterval = downloadIntervalString?.toIntOrNull() ?: DEFAULT_INTERVAL

        val downloadUrl = "$url"

        // Show a toast message with the download URL
        Toast.makeText(this, "Downloading questions from: $downloadUrl", Toast.LENGTH_LONG).show()


        Log.d("MainActivity", "Question Data URL: $url")
        Log.d("MainActivity", "Refresh Interval: $downloadInterval")

        if (downloadUrl.isNotEmpty()) {
            executor.execute {
                downloadFile(downloadUrl)
            }
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

    // Background task to download the JSON file
    private fun downloadFile(urlString: String) {
        val urlConnection = URL(urlString).openConnection() as HttpURLConnection
        val responseStream = ByteArrayOutputStream()
        try {
            showProgressDialog()
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val reader = inputStream.bufferedReader()
            reader.useLines { lines ->
                lines.forEach { line ->
                    responseStream.write(line.toByteArray())
                }
            }
        } catch (e: Exception) {
            // Log the error
            Log.e("MainActivity", "Error downloading questions", e)
            showToast("Download failed. Please retry later.")
            runOnUiThread { showDownloadFailedDialog() }
        } finally {
            urlConnection.disconnect()
        }
        // Now that we have the complete response, save it to a local file with a custom name
        saveToFile(responseStream.toByteArray(), "questions.json")
        showToast("Download succeeded!")
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDownloadFailedDialog() {
        // Create a handler for the main/UI thread
        val handler = Handler(Looper.getMainLooper())

        // Post a Runnable to the main/UI thread's message queue
        handler.post {
            val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
            alertDialogBuilder.setTitle("Download Failed")
            alertDialogBuilder.setMessage("Failed to download questions. Do you want to retry or close?")
            alertDialogBuilder.setPositiveButton("Retry") { dialog, which ->
                // Retry the download
                val url = preferences.getString("pref_key_url", "")
                executor.execute {
                    if (url != null) {
                        downloadFile(url)
                    }
                }
                dialog.dismiss() // Dismiss the dialog after retrying
            }
            alertDialogBuilder.setNegativeButton("Close") { dialog, which ->
                // Close the AlertDialog
                dialog.dismiss()
            }
            alertDialogBuilder.show()
        }
    }


    private fun saveToFile(data: ByteArray, fileName: String) {
        val outputFile = File(filesDir, fileName)
        FileOutputStream(outputFile).use { outputStream ->
            outputStream.write(data)
        }
        Log.i("MainActivity", "Download completed")
        dismissProgressDialog()
    }

    private fun checkNetworkStatus() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            // No network connection
            handleNoNetworkConnection()
        } else if (networkInfo.type == ConnectivityManager.TYPE_WIFI ||
            networkInfo.type == ConnectivityManager.TYPE_MOBILE
        ) {
        }
    }

    private fun handleNoNetworkConnection() {
        // Check if the device is in airplane mode
        if (isAirplaneModeOn()) {
            // Display a dialog asking if the user wants to turn off airplane mode
            // and navigate to the settings activity if the user chooses to do so
            showAirplaneModeDialog()
        } else {
            // Display a Snackbar indicating no network connection
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "You have no access to the Internet. Please check your network connection.",
                Snackbar.LENGTH_LONG
            )
            snackbar.show()
        }
    }

    private fun isAirplaneModeOn(): Boolean {
        return Settings.System.getInt(
            contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }

    private fun showAirplaneModeDialog() {
        AlertDialog.Builder(this)
            .setTitle("Airplane Mode Detected")
            .setMessage("Airplane mode is currently on. Do you want to turn it off?")
            .setPositiveButton("Yes") { dialog, _ ->
                // User clicked Yes, navigate to airplane mode settings
                val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                // User clicked No, dismiss the dialog
                dialog.dismiss()
            }
            .setCancelable(false) // Prevent dialog dismissal on outside touch or back button press
            .show()
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
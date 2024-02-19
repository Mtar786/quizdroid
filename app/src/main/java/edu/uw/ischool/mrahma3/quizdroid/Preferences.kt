package edu.uw.ischool.mrahma3.quizdroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Preferences : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }
}
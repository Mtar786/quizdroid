package edu.uw.ischool.mrahma3.quizdroid

import androidx.lifecycle.ViewModel

class QuizViewModel: ViewModel() {
    var correctCount: Int = 0

    fun resetCorrectCount() {
        correctCount = 0
    }
}
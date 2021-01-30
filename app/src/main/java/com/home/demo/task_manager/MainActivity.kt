package com.home.demo.task_manager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rollButton: Button = findViewById(R.id.roll_button)
        val countUpButton: Button = findViewById(R.id.count_up)
        val resetButton: Button = findViewById(R.id.reset)
        val resultText: TextView = findViewById(R.id.result_text)
        rollButton.setOnClickListener { rollDice(resultText) }
        countUpButton.setOnClickListener { countUp(resultText) }
        resetButton.setOnClickListener { reset(resultText) }
    }

    private fun rollDice(resultText: TextView) {
        val randomInt = (1..6).random()
        resultText.text = randomInt.toString()
        Toast.makeText(
            this, "button clicked",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun countUp(resultText: TextView) {
        if (resultText.text.toString() == "Hello World!") {
            resultText.text = "1"
        } else if (resultText.text.toString() != "6") {
            resultText.text = (resultText.text.toString().toInt() + 1).toString()
        }
    }

    private fun reset(resultText: TextView) {
        resultText.text = "0"
    }
}
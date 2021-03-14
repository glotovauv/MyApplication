package com.home.demo.task_manager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    lateinit var diceImage : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "This FAB needs an action!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val rollButton: Button = findViewById(R.id.roll_button)
        val countUpButton: Button = findViewById(R.id.count_up)
        val resetButton: Button = findViewById(R.id.reset)
        diceImage = findViewById(R.id.dice_image)
        rollButton.setOnClickListener { rollDice() }
        countUpButton.setOnClickListener { countUp() }
        resetButton.setOnClickListener { reset() }

        val toSecondScreen: Button = findViewById(R.id.second_screen)
        val toTestScreen: Button = findViewById(R.id.test_screen)

        toSecondScreen.setOnClickListener {
            run {
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                startActivity(intent)
            }
        }

        toTestScreen.setOnClickListener {
            run {
                val intent = Intent(this@MainActivity, TestActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun rollDice() {
        val randomInt = (1..6).random()
        val tag : Int
        val drawableResource = when (randomInt) {
            1 -> {
                tag = 1
                R.drawable.dice_1
            }
            2 -> {
                tag = 2
                R.drawable.dice_2
            }
            3 -> {
                tag = 3
                R.drawable.dice_3
            }
            4 -> {
                tag = 4
                R.drawable.dice_4
            }
            5 -> {
                tag = 5
                R.drawable.dice_5
            }
            else -> {
                tag = 6
                R.drawable.dice_6
            }
        }
        diceImage.setImageResource(drawableResource)
        diceImage.tag = tag
        Toast.makeText(
            this, "button clicked",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun countUp() {
        val tag : Int
        val image : Int = when (diceImage.tag) {
            1 -> {
                tag = 2
                R.drawable.dice_2
            }
            2 -> {
                tag = 3
                R.drawable.dice_3
            }
            3 -> {
                tag = 4
                R.drawable.dice_4
            }
            4 -> {
                tag = 5
                R.drawable.dice_5
            }
            5 -> {
                tag = 6
                R.drawable.dice_6
            }
            else -> {
                tag = 1
                R.drawable.dice_1
            }
        }
        diceImage.setImageResource(image)
        diceImage.tag = tag
    }

        private fun reset() {
        diceImage.setImageResource(R.drawable.empty_dice)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
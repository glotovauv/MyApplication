package com.home.demo.task_manager.sea.battle.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.home.demo.task_manager.sea.battle.view.FieldPrepareView
import com.home.demo.task_manager.R

class SeaBattleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sea_battle_layout)
        val fieldView =
            FieldPrepareView(this)
        fieldView.setBackgroundColor(Color.WHITE)
        val container = findViewById<LinearLayout>(R.id.container)
        container.addView(fieldView)

        val right = findViewById<Button>(R.id.right)
        val left = findViewById<Button>(R.id.left)
        val up = findViewById<Button>(R.id.up)
        val down = findViewById<Button>(R.id.down)
        val rotate = findViewById<Button>(R.id.rotate)
        val reset = findViewById<Button>(R.id.reset)
        val add1 = findViewById<Button>(R.id.add1)
        val add2 = findViewById<Button>(R.id.add2)
        val add3 = findViewById<Button>(R.id.add3)
        val add4 = findViewById<Button>(R.id.add4)

        right.setOnClickListener { fieldView.move(1, 0) }
        left.setOnClickListener { fieldView.move(-1, 0) }
        up.setOnClickListener { fieldView.move(0, -1) }
        down.setOnClickListener { fieldView.move(0, 1) }
        rotate.setOnClickListener { fieldView.rotate() }
        reset.setOnClickListener { fieldView.reset() }
        add1.setOnClickListener { fieldView.addShipToField(1) }
        add2.setOnClickListener { fieldView.addShipToField(2) }
        add3.setOnClickListener { fieldView.addShipToField(3) }
        add4.setOnClickListener { fieldView.addShipToField(4) }
    }
}
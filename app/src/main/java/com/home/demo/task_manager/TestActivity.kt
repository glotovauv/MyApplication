package com.home.demo.task_manager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity: AppCompatActivity() {

    private lateinit var checkBox: CheckBox
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        println("on create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_layout)
        setSupportActionBar(findViewById(R.id.test_toolbar))
        textView = findViewById(R.id.textView)
        checkBox = findViewById(R.id.chbExtMenu)

        findViewById<Button>(R.id.first_screen).setOnClickListener{finish()}
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        println("prepare options menu")
        menu?.setGroupVisible(R.id.group1, checkBox.isChecked)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        println("on create options menu")
        /*menu?.add(0, 1, 0, "add")
        menu?.add(0, 2, 0, "edit")
        menu?.add(0, 3, 3, "delete")
        menu?.add(1, 4, 1, "copy")
        menu?.add(1, 5, 2, "paste")
        menu?.add(1, 6, 4, "exit")*/
        menuInflater.inflate(R.menu.mymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("item selected")
        val sb = StringBuilder()
        sb.append("Item Menu")
        sb.append("\r\n groupId: " + item.groupId.toString())
        sb.append("\r\n itemId: " + item.itemId.toString())
        sb.append("\r\n order: " + item.order.toString())
        sb.append("\r\n title: " + item.title)
        textView.text = sb.toString()

        return super.onOptionsItemSelected(item)
    }
}
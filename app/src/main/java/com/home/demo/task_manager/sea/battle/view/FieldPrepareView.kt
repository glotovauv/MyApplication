package com.home.demo.task_manager.sea.battle.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import com.home.demo.task_manager.sea.battle.service.TrackService
import com.home.demo.task_manager.sea.battle.service.CommandService
import com.home.demo.task_manager.sea.battle.model.TempField
import com.home.demo.task_manager.sea.battle.fieldSize

class FieldPrepareView(context: Context) : View(context) {
    private val paint = Paint()
    private val LIGHT_BLUE = Color.argb(80, 102, 204, 255)

    private val delta = 90
    private val start = 100F
    private val stop = delta * fieldSize + start

    private var tempField =
        TempField(
            start.toInt(),
            delta
        )
    private val service =
        CommandService(tempField)
    private val tracker =
        TrackService(tempField)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Log.i("onTouchEvent", event.toString())

        event?.let {
            val pointerId = it.getPointerId(it.actionIndex)
            when (it.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    tracker.move(this, it.x.toInt(), it.y.toInt(), pointerId)
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    tracker.down(it.x.toInt(), it.y.toInt(), pointerId)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    tracker.up(pointerId)
                }
                MotionEvent.ACTION_DOWN -> {
                    tracker.handleActionDown(it.x.toInt(), it.y.toInt(), pointerId)
                }
                MotionEvent.ACTION_UP -> {
                    tracker.handleActionUp()
                }
            }
        }
        return true
    }

    fun addShipToField(size: Int) {
        refresh(service.addShip(size))
    }

    fun reset() {
        service.clearField()
        invalidate()
    }

    fun move(dx: Int, dy: Int) {
        refresh(service.move(dx, dy))
    }

    fun rotate() {
        refresh(service.rotate())
    }

    private fun refresh(isActionSuccess: Boolean) {
        if (isActionSuccess) invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawSavedShips(canvas)
            drawSelectedShip(canvas)
            drawGrid(canvas)
            drawTrack(canvas)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        paint.color = Color.BLUE
        for (i in 0..delta * 10 step delta) {
            canvas.drawLine(start + i, start, start + i, stop, paint);
        }
        for (i in 0..delta * 10 step delta) {
            canvas.drawLine(start, start + i, stop, start + i, paint);
        }
    }

    private fun drawSavedShips(canvas: Canvas) {
        paint.color = Color.GREEN
        for ((idx, pair) in tempField.shipList.withIndex()) {
            if (idx != tempField.currentNumber) {
                canvas.drawRect(pair.second, paint)
            }
        }
    }

    private fun drawSelectedShip(canvas: Canvas) {
        paint.color = if (tempField.isCorrectLocation) LIGHT_BLUE else Color.RED
        tempField.currentRect?.let { canvas.drawRect(it, paint) }
    }

    private fun drawTrack(canvas: Canvas) {
        if (tracker.trackPoint != null) {
            canvas.drawRect(tracker.trackRect, tracker.trackPaint)
        }
    }

}
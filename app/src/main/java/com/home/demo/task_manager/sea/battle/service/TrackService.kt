package com.home.demo.task_manager.sea.battle.service

import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import com.home.demo.task_manager.sea.battle.view.FieldPrepareView
import com.home.demo.task_manager.sea.battle.model.TempField

class TrackService(private val field: TempField) {

    val trackPaint = Paint()
    var trackRect = RectF(0F, 0F, 0F, 0F)
    var trackPoint: Pair<Int, Int>? = null
    private var trackMoveId = -1
    private var trackRotateId = -1
    private val touchIdToShipNumber: MutableMap<Int, Int> = HashMap()

    private var lastClickTime = 0L
    private var doubleClickInterval = 200L
    private var lastNumber = -1

    init {
        trackPaint.style = Paint.Style.STROKE
        trackPaint.color = Color.BLACK
        trackPaint.strokeWidth = 4F
        trackPaint.pathEffect = DashPathEffect(floatArrayOf(30F, 10F), 0F)
    }

    fun reset() {
        trackPaint.color = Color.BLACK
        trackPoint = null
        trackMoveId = -1
        trackRotateId = -1
    }

    fun move(view: FieldPrepareView, eventX: Int, eventY: Int, pointerId: Int) {
        var (x, y) = field.findTopOfCell(eventX, eventY)
        field.getShipNumberIfExist(x, y)?.let {
            touchIdToShipNumber[pointerId] = it
        }
        if (trackMoveId != pointerId && trackRotateId != pointerId) {
            return
        }
        trackPoint?.let {
            if (it.first != x || it.second != y) {
                val rightBorder = field.stop - (trackRect.right - it.first)
                val leftBorder = field.start + it.first - trackRect.left
                val topBorder = field.start + it.second - trackRect.top
                val bottomBorder = field.stop - (trackRect.bottom - it.second)
                x = x.coerceIn(leftBorder.toInt(), rightBorder.toInt())
                y = y.coerceIn(topBorder.toInt(), bottomBorder.toInt())
                trackRect.offset(
                    (x - it.first).toFloat(),
                    (y - it.second).toFloat()
                )
                if (field.isPositionCorrect(trackRect)) {
                    trackPaint.color = Color.BLACK
                } else {
                    trackPaint.color = Color.RED
                }
                trackPoint = x to y
                Log.i("change trackPoint", "$x $y")
                view.invalidate()
            }
        }
    }

    fun down(eventX: Int, eventY: Int, pointerId: Int) {
        if (field.isCurrentRect(eventX, eventY)) {
            if (trackRotateId == -1) trackRotateId = pointerId
        }
    }

    fun handleActionDown(eventX: Int, eventY: Int, pointerId: Int) {
      /*  show(field.getShipPositionIfExist(eventX, eventY), pointerId)
        field.selectShip(eventX, eventY)
        field.currentRect?.apply {
            if (isDoubleClick(field.currentNumber)) rotate()
            else paint.color = LIGHT_BLUE
            copyRect(this, tracker.trackRect)
        }
        invalidate()*/
    }

    fun handleActionUp() {
 /*       if (trackPoint != null && field.insideBorders(trackRect)
            && field.isPositionCorrect(trackRect)
        ) {
            field.currentRect?.apply {
                copyRect(trackRect, this)
                Log.i("save track", trackRect.toString())
                field.savePosition()
            }

        }
        reset()
        invalidate()*/
    }

    private fun copyRect(from: RectF, to: RectF) {
        to.left = from.left
        to.right = from.right
        to.top = from.top
        to.bottom = from.bottom
    }

    fun up(pointerId: Int) {
        //select new ship if other touch on empty field
      /*  if (pointerId == trackMoveId) {
            //save changes???
            trackMoveId = getFreeId(this)
            if (trackMoveId == -1 && isTouchOnCurrentShip(trackMoveId)) {
                trackMoveId = trackRotateId
                trackRotateId = -1
            }
        } else if (pointerId == trackRotateId) {
            //save changes???
            trackRotateId = getFreeId(this)
        }*/
    }

    private fun getFreeId(event: MotionEvent): Int {
        for (i in 0 until event.pointerCount) {
            val id = event.getPointerId(i)
            if (id != trackMoveId && id != trackRotateId && isTouchOnCurrentShip(id)) {
                return id
            }
        }
        return -1
    }

    private fun isTouchOnCurrentShip(id: Int) = touchIdToShipNumber[id] == field.currentNumber

    fun show(shipPosition: Pair<Int, Int>?, pointerId: Int) {
        trackPoint = shipPosition
        Log.i("init trackPoint", trackPoint.toString())
        if (trackPoint != null) trackMoveId = pointerId
    }

    fun draw(canvas: Canvas) {
        if (trackPoint != null) {
            canvas.drawRect(trackRect, trackPaint)
        }
    }

    fun isDoubleClick(currentNumber: Int): Boolean {
        val clickInterval = System.currentTimeMillis() - lastClickTime

        val doubleClick = clickInterval <= doubleClickInterval && lastNumber != -1
                && currentNumber == lastNumber
        Log.i("clickInterval", clickInterval.toString())

        lastNumber = currentNumber
        lastClickTime += clickInterval

        return doubleClick
    }

}
package com.home.demo.task_manager

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.transform
import com.home.demo.task_manager.test.Ship
import com.home.demo.task_manager.test.TempField
import com.home.demo.task_manager.test.fieldSize

class SeaBattleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sea_battle_layout)
        val drawView = DrawView(this)
        drawView.setBackgroundColor(Color.WHITE)
        val container = findViewById<LinearLayout>(R.id.container)
        container.addView(drawView)

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

        right.setOnClickListener { drawView.move(1, 0) }
        left.setOnClickListener { drawView.move(-1, 0) }
        up.setOnClickListener { drawView.move(0, -1) }
        down.setOnClickListener { drawView.move(0, 1) }
        rotate.setOnClickListener { drawView.rotate() }
        reset.setOnClickListener { drawView.reset() }
        add1.setOnClickListener { drawView.addShipToField(1) }
        add2.setOnClickListener { drawView.addShipToField(2) }
        add3.setOnClickListener { drawView.addShipToField(3) }
        add4.setOnClickListener { drawView.addShipToField(4) }
    }

    class DrawView(context: Context) : View(context) {
        private val gridPaint = Paint()
        private val shipPaint = Paint()
        private val savedPaint = Paint()
        private val trackPaint = Paint()

        private val delta = 90
        private val start = 100F
        private val stop = delta * fieldSize + start

        private var tempField = TempField(start.toInt(), delta)
        private val transformMatrix = Matrix()

        private var trackRect = RectF(start, start, start + delta * 4, start + delta)
        var trackPoint: Pair<Int, Int>? = null
        private var trackMoveId = -1
        private var trackRotateId = -1

        private var lastClickTime = 0L
        private var doubleClickInterval = 200L
        private var lastNumber = -1

        init {
            savedPaint.color = Color.GREEN
            gridPaint.color = Color.BLUE
            trackPaint.style = Paint.Style.STROKE
            trackPaint.strokeWidth = 4F
            trackPaint.pathEffect = DashPathEffect(floatArrayOf(30F, 10F), 0F)
        }

        fun addShipToField(size: Int) {
            tempField.saveAndReset()
            if (tempField.isAvailableToAdd(size)) {
                val ship = Ship(size)
                val (x, y) = ship.getX() + start to ship.getY() + start
                val rect = RectF(x, y, delta * ship.size + x, delta + y)
                tempField.addShip(ship, rect)
                checkShip()
            }
            invalidate()
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            // Log.i("onTouchEvent", event.toString())

            event?.apply {
                val pointerId = getPointerId(actionIndex)
                when (actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        handleActionMove(x.toInt(), y.toInt())
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> {
                        if (trackRotateId == -1) {
                            trackRotateId = pointerId
                        }
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                        if (pointerId == trackMoveId) {
                            trackMoveId = getFreeId(this)
                            if (trackMoveId == -1) {
                                trackMoveId = trackRotateId
                                trackRotateId = -1
                            }
                        } else if (pointerId == trackRotateId) {
                            trackRotateId = getFreeId(this)
                        }
                    }
                    MotionEvent.ACTION_DOWN -> {
                        trackMoveId = pointerId
                        handleActionDown(x.toInt(), y.toInt())
                    }
                    MotionEvent.ACTION_UP -> {
                        trackMoveId = -1
                        handleActionUp()
                    }
                }
            }
            return true
        }

        private fun getFreeId(event: MotionEvent): Int {
            for (i in 0 until event.pointerCount) {
                val id = event.getPointerId(i)
                if (id != trackMoveId && id != trackRotateId) {
                    return id
                }
            }
            return -1
        }

        private fun handleActionMove(eventX: Int, eventY: Int) {
            trackPoint?.let {
                var (x, y) = tempField.findTopOfCell(eventX, eventY)
                if (it.first != x || it.second != y) {
                    val rightBorder = stop - (trackRect.right - it.first)
                    val leftBorder = start + it.first - trackRect.left
                    val topBorder = start + it.second - trackRect.top
                    val bottomBorder = stop - (trackRect.bottom - it.second)
                    x = x.coerceIn(leftBorder.toInt(), rightBorder.toInt())
                    y = y.coerceIn(topBorder.toInt(), bottomBorder.toInt())
                    trackRect.offset(
                        (x - it.first).toFloat(),
                        (y - it.second).toFloat()
                    )
                    if (tempField.isPositionCorrect(trackRect)) {
                        trackPaint.color = Color.BLACK
                    } else {
                        trackPaint.color = Color.RED
                    }
                    trackPoint = x to y
                    Log.i("change trackPoint", "$x $y")
                    invalidate()
                }
            }
        }

        private fun handleActionDown(eventX: Int, eventY: Int) {
            trackPoint = tempField.getShipPositionIfExist(eventX, eventY)
            Log.i("init trackPoint", trackPoint.toString())
            tempField.selectShip(eventX, eventY)
            tempField.currentRect?.apply {
                if (isDoubleClick()) rotate()
                else shipPaint.color = Color.argb(80, 102, 204, 255)
                copyRect(this, trackRect)
            }
            invalidate()
        }

        private fun isDoubleClick(): Boolean {
            val clickInterval = System.currentTimeMillis() - lastClickTime

            val doubleClick = clickInterval <= doubleClickInterval && lastNumber != -1
                    && tempField.currentNumber == lastNumber
            Log.i("clickInterval", clickInterval.toString())

            lastNumber = tempField.currentNumber
            lastClickTime += clickInterval

            return doubleClick
        }

        private fun handleActionUp() {
            if (trackPoint != null && insideBorders(trackRect)
                && tempField.isPositionCorrect(trackRect)
            ) {
                tempField.currentRect?.apply {
                    copyRect(trackRect, this)
                    Log.i("save track", trackRect.toString())
                    tempField.savePosition()
                }

            }
            trackPaint.color = Color.BLACK
            trackPoint = null
            trackMoveId = -1
            trackRotateId = -1
            invalidate()
        }

        private fun copyRect(from: RectF, to: RectF) {
            to.left = from.left
            to.right = from.right
            to.top = from.top
            to.bottom = from.bottom
        }

        private fun checkShip() {
            tempField.currentRect?.apply {
                if (!insideBorders(this) || !tempField.isPositionCorrect(this)) {
                    shipPaint.color = Color.RED
                } else {
                    shipPaint.color = Color.argb(80, 102, 204, 255)
                }
            }
        }

        private fun insideBorders(rect: RectF, offsetX: Int = 0, offsetY: Int = 0): Boolean {
            return rect.left + offsetX >= start && rect.top + offsetY >= start
                    && rect.right + offsetX <= stop && rect.bottom + offsetY <= stop
        }

        fun save() {
            tempField.saveAndReset()
            invalidate()
        }

        fun reset() {
            //tempField = TempField(start.toInt(), delta)
            tempField.clearField()
            invalidate()
        }

        fun move(dx: Int, dy: Int) {
            tempField.currentRect?.apply {
                val offsetX = dx * delta
                val offsetY = dy * delta
                if (insideBorders(this, offsetX, offsetY)) {
                    offset(offsetX.toFloat(), offsetY.toFloat())
                    if (tempField.isPositionCorrect(this)) {
                        tempField.savePosition()
                    }
                    checkShip()
                    invalidate()
                }
            }
        }

        fun rotate() {
            tempField.currentRect?.apply {
                transformMatrix.reset()
                if (tempField.isVertical) {
                    transformMatrix.setRotate(-90F, left, top)
                    transformMatrix.postTranslate(0F, delta.toFloat())
                } else {
                    transformMatrix.setRotate(90F, left, top)
                    transformMatrix.postTranslate(delta.toFloat(), 0F)
                }
                this.transform(transformMatrix)
                tempField.isVertical = !tempField.isVertical
                correctAfterRotate(this)
                checkShip()
                invalidate()
            }
        }

        private fun correctAfterRotate(rect: RectF) {
            if (rect.right > stop) {
                rect.offset(stop - rect.right, 0F)
            } else if (rect.bottom > stop) {
                rect.offset(0F, stop - rect.bottom)
            }
        }

        override fun onDraw(canvas: Canvas?) {
            tempField.currentRect?.apply {
                canvas?.drawRect(this, shipPaint)
            }
            canvas?.let {
                drawSaved(canvas)
                tempField.currentRect?.let { canvas.drawRect(it, shipPaint) }
                drawGrid(canvas)
                if (trackPoint != null) {
                    canvas.drawRect(trackRect, trackPaint)
                }
            }
        }

        private fun drawGrid(canvas: Canvas) {
            for (i in 0..delta * 10 step delta) {
                canvas.drawLine(start + i, start, start + i, stop, gridPaint);
            }
            for (i in 0..delta * 10 step delta) {
                canvas.drawLine(start, start + i, stop, start + i, gridPaint);
            }
        }

        private fun drawSaved(canvas: Canvas) {
            for ((idx, pair) in tempField.shipList.withIndex()) {
                if (idx != tempField.currentNumber) {
                    canvas.drawRect(pair.second, savedPaint)
                }
            }
        }
    }
}
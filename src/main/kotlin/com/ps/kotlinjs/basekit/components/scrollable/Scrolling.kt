package com.ps.kotlinjs.basekit.components.scrollable

import kotlin.js.Date

class Scrolling(private var startTime : Int,
                private var startX : Int,
                private var startY : Int) {

    private lateinit var animStartTime : Date
    private val acceleration = -0.0008    //      px/s2

    private var velocityX: Double = 0.0   //     px/ms
    private var velocityY: Double = 0.0   //     px/ms
    private var v = 0.0

    fun finalize(endTime : Int,
                  endX : Int,
                  endY : Int) {

        var ms = endTime - startTime
        val dx = (endX - startX).toDouble()
        val dy = (endY - startY).toDouble()

        velocityX = dx.toDouble()/ms
        velocityY = dy.toDouble()/ms
        animStartTime = Date()
    }

    fun getPositionInTime(time: Date) : List<Double> {
        val d = getDuration()
        val t = time.getTime() - animStartTime.getTime()

        var aX = -velocityX/d
        var vX = velocityX

        var aY = -velocityY/d
        var vY = velocityY

        val x = js("(vX * t) + (1/2 * aX * t*t)")
        val y = js("(vY * t) + (1/2 * aY * t*t)")

        return listOf(x,y)
    }

    fun getDuration() : Double {
        val v = kotlin.math.sqrt((velocityX * velocityX) + (velocityY * velocityY))

        return -v / acceleration
    }
}
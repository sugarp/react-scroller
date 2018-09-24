package com.ps.demo.helpers

import com.ps.kotlinjs.basekit.decorators.addHours
import com.ps.kotlinjs.basekit.decorators.addMinutes
import com.ps.kotlinjs.basekit.decorators.clearTime
import kotlin.js.Date

object DateHelper {
    fun getFromTo(from: Date, to: Date) : String {
        return "${zeroize(from.getHours())}:${zeroize(from.getMinutes())} - ${zeroize(to.getHours())}:${zeroize(to.getMinutes())}"
    }

    fun zeroize(v: Int) : String {
        var str = v.toString()

        if(str.length == 1) {
            return "0$str"
        } else {
            return str
        }
    }

    fun getRoundedToClosestTimeSlot(date: Date, nextSlotConstant: Int = 5) : Date {
        var minutes = date.getMinutes() % 60
        var hours = date.getHours()
        val timeToNextSlot = nextSlotConstant - minutes % nextSlotConstant

        if (timeToNextSlot != 5) {
            minutes += timeToNextSlot
        }
        if (minutes == 60) {
            minutes = 0;
            if (hours < 23) {
                hours++;
            }
        }

        return date.clearTime().addHours(hours).addMinutes(minutes)
    }

    fun getClockTime(d : Date) : String{
        return "${zeroize(d.getHours())}:${zeroize(d.getMinutes())}"
    }

    fun clearTime(date: Date) : Date {
        return date;
    }
}
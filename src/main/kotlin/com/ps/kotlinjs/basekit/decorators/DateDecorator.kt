package com.ps.kotlinjs.basekit.decorators

import kotlin.js.Date

fun Date.addDays(days: Int) : Date {
    var time = this.getTime()
    var dayTime = 1000 * 60 * 60 * 24

    return Date(time + dayTime * days)
}

fun Date.addHours(hours: Int) : Date {
    return Date(this.getTime() + 1000 * 60 * 60 * hours)
}

fun Date.addMinutes(mins: Int) : Date {
    return Date(this.getTime() + 1000 * 60 * mins)
}

fun Date.addSeconds(mins: Int) : Date {
    return Date(this.getTime() + 1000 * mins)
}

fun Date.addMilliseconds(millis: Int) : Date {
    return Date(this.getTime() + millis)
}

fun Date.clearTime() : Date {
    val date = js("arguments[0]")
    return js("new Date(new Date(date.getTime()).setHours(0,0,0,0))") as Date
}

fun Date.clearHour() : Date {
    val date = js("arguments[0]")
    return js("new Date(new Date(date.getTime()).setHours(date.getHours(),0,0,0))") as Date
}

operator fun Date.minus(d2: Date) : Int {
    var d1 : Date = js("arguments[0]")
    return (d1.getTime() - d2.getTime()).toInt()
}

operator fun Date.compareTo(d2: Date) : Int {
    var d1 : Date = js("arguments[0]")
    return (d1.getTime() - d2.getTime()).toInt()
}
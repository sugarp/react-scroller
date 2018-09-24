package com.ps.kotlinjs.basekit.decorators

import org.w3c.dom.events.Event

fun Event.getCoords():List<Int> {
    var e = this.asDynamic()
    var type = e["type"]

    if (type.indexOf("mouse") > -1) {
//                console.log("coords:", e.clientX, e.clientY)
        return listOf(e.clientX, e.clientY)
    } else if (type.indexOf("touch") > -1) {
//                console.log("coords:", e.touches[0].clientX, e.touches[0].clientY)
        return listOf(e.touches[0].clientX, e.touches[0].clientY)
    } else {
        throw IllegalArgumentException("Unknown event type.")
    }
}
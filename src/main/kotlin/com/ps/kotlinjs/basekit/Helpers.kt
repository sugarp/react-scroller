package com.ps.kotlinjs.basekit

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Math

object Helpers {
    fun getEventCoords(event: Event):List<Int> {
        var e = event.asDynamic()
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

    fun stopEvent(e: Event) {
        e.preventDefault()
        e.stopPropagation()

        var fn = e.asDynamic()["stopImmediatePropagation"]

        if(fn != null) {
            e.stopImmediatePropagation()
        }
    }

    fun getStyle(styles : Map<String, String>) : String {
        var r = ""

        styles.forEach {
            r += it.key + ":" + it.value + ";"
        }

        return r
    }


//        return {
//            fontSize: Math.min(window.innerHeight * windowHeightRation, ((maxWidth / duration) * fontSize) -1.5),
//            marginTop: fontSize / 11.5
//        }
    fun getFontSize(text: String, maxWidth: Int, fontFamily: String? = null) : List<Double> {
        var canvas = document.createElement("canvas") as HTMLCanvasElement
        var ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        var fontSize = 10
        var vmin = 0.14
        var r = Math.min(window.innerHeight, window.innerWidth)


        if(fontFamily != null) {
            ctx.font = "${fontSize}px $fontFamily"
        }

        var width = ctx.measureText(text).width

        return listOf(Math.min(r * vmin, ((maxWidth / width) * fontSize) -1.5), fontSize / 11.5)
    }
}
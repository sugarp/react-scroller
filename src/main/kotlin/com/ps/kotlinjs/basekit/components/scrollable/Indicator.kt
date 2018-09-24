package com.ps.kotlinjs.basekit.components.scrollable

import com.ps.kotlinjs.basekit.Helpers
import com.ps.kotlinjs.basekit.Scrollable
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import kotlin.browser.document

class Indicator(private var el: Element,
                private var direction: Direction,
                private var scrollable: Scrollable,
                private var viewSize: Int,
                private var contentSize: Int) {

    enum class Direction {
        vertical,
        horizontal
    }

    private var dragStartIndicatorPos = 0
    private var indicatorPos: Int = 0
    private var size : Int = viewSize / (contentSize / viewSize)
    private var max = viewSize - size
    private var min = 0

    init {
        el.addEventListener("mousedown", {onTouchStart(it)})
        el.addEventListener("touchstart", {onTouchStart(it)})

        setRatio(0.0, false)
    }

    var position: Double = 0.0
    var dragStartPos = 0

    fun onTouchStart(e: Event) {
        dragStartIndicatorPos = indicatorPos
        dragStartPos = if(direction == Direction.horizontal) Helpers.getEventCoords(e)[0] else Helpers.getEventCoords(e)[1]

        document.body?.onmousemove = {onTouchMove(it)}
        document.body?.asDynamic().ontouchmove =  { event: Event -> onTouchMove(event)}

        document.body?.onmouseup = {onTouchEnd(it)}
        document.body?.asDynamic().ontouchend = { event: Event -> onTouchEnd(event)}

        e.stopPropagation()
        e.preventDefault()
    }

    fun onTouchMove(e: Event) {
        val coordinate = if(direction == Direction.horizontal) Helpers.getEventCoords(e)[0] else Helpers.getEventCoords(e)[1]
        val d = coordinate - dragStartPos

        var pos = dragStartIndicatorPos + d
        var ratio = pos / max.toDouble()

        if(direction == Direction.horizontal) {
            scrollable.scrollToRatio(ratio, null)
        } else {
            scrollable.scrollToRatio(null, ratio)
        }

        e.stopPropagation()
        e.preventDefault()
    }

    fun onTouchEnd(e: Event) {
        document.body?.onmousemove = null
        document.body?.asDynamic().ontouchmove =  null

        document.body?.onmouseup = null
        document.body?.asDynamic().ontouchend = null

        e.stopPropagation()
        e.preventDefault()
    }

    fun setRatio(ratio: Double, animate: Boolean) {
        var pos = ratio * (viewSize - size)

//        console.log("ind:", size, ratio, pos, direction)
        if(pos < min) {
            position = min.toDouble()
        } else if(pos > max) {
            position = max.toDouble()
        } else {
            position = pos
        }

        if(direction == Direction.vertical) {
            var styles =
                    if(animate) "transform:translate3d(0,${position}px,0);height:${size}px;${Scrollable.scrollToTransition}"
                    else "transform:translate3d(0,${position}px,0);height:${size}px;"

            el.setAttribute("style", styles)
        } else {
            var styles =
                    if(animate) "transform:translate3d(${position}px,0,0);width:${size}px;${Scrollable.scrollToTransition}"
                    else "transform:translate3d(${position}px,0,0);width:${size}px;"

            el.setAttribute("style", styles)
        }

        indicatorPos = pos.toInt()
    }
}
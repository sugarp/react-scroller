package com.ps.kotlinjs.demo.freeBusyTable

import com.ps.kotlinjs.basekit.Helpers
import com.ps.kotlinjs.basekit.decorators.addMinutes
import com.ps.kotlinjs.basekit.decorators.setStyles
import com.ps.kotlinjs.basekit.types.Duration
import com.ps.kotlinjs.demo.freeBusyTable.FreeBusyTable.Companion.convertMinutesToPx
import com.ps.kotlinjs.demo.freeBusyTable.FreeBusyTable.Companion.convertPxToMinutes
import com.ps.kotlinjs.demo.freeBusyTable.FreeBusyTable.Companion.getDate
import com.ps.kotlinjs.demo.freeBusyTable.FreeBusyTable.Companion.snapPxToMinutes
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onTouchStartFunction
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.jsStyle
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Math.random

class DurationBar : RComponent<DurationBar.Props, DurationBar.State>() {
    interface Props : RProps {
        var start: Date
        var timeLineStart: Date
        var hourWidth: Int
        var duration: Int

        var onChangeStart: () -> Unit
        var onChange: (Date, Int) -> Unit
        var onChangeEnd: () -> Unit

        var onMount: (DurationBar) -> Unit
    }

    interface State : RState {

    }

    private val MIN_DURATION = 15

    private lateinit var el : Element
    private lateinit var leftBorderEl : Element
    private lateinit var rightBorderEl : Element

    private var dragStartWidth = -1
    private var dragStartLeft = -1
    private var dragStartX = -1

    private var currentWidth = -1
    private var currentLeft = -1

    private fun getLeft(date: Date) : Double {
        var hoursDiff = Duration(props.timeLineStart, date).getHours()

        return props.hourWidth * hoursDiff
    }


    //region currentLeft border
    private fun onLeftDragStart(e: Event) : Boolean{
        var coords = Helpers.getEventCoords(e)
        dragStartX = coords[0]
        dragStartWidth = currentWidth
        dragStartLeft = currentLeft

        document.body?.onmousemove = {onLeftDragMove(it)}
        document.body?.asDynamic().ontouchmove =  {event: Event -> onLeftDragMove(event)}

        document.body?.onmouseup = {onLeftDragEnd(it)}
        document.body?.asDynamic().ontouchend = {event: Event -> onLeftDragEnd(event)}

        props.onChangeStart()

        Helpers.stopEvent(e)
        return false
    }

    private var onLeftDragMove: (e: Event) -> Boolean = {
        var coords = Helpers.getEventCoords(it)
        var d = snapPxToMinutes(coords[0] - dragStartX, props.hourWidth)
        var minWidth = convertMinutesToPx(MIN_DURATION, props.hourWidth)
        var maxLeft = this.dragStartLeft + this.dragStartWidth - convertMinutesToPx(props.hourWidth, MIN_DURATION)

        var left = this.dragStartLeft + d

        if(left <= maxLeft) {
            this.currentLeft = left
            this.currentWidth = this.dragStartWidth - d
        } else {
            this.currentLeft = maxLeft
            this.currentWidth = minWidth
        }

        var date = getDate(this.currentLeft,props.hourWidth,props.timeLineStart)
        var duration = convertPxToMinutes(props.hourWidth, this.currentWidth)

        el.setStyles("width:${currentWidth}px;left:${currentLeft}px;")
        props.onChange(date, duration)

        Helpers.stopEvent(it)
        false
    }

    private var onLeftDragEnd: (e: Event) -> Boolean = {
        setState {  }

        document.body?.onmousemove = null
        document.body?.asDynamic().ontouchmove =  null

        document.body?.onmouseup = null
        document.body?.asDynamic().ontouchend = null

        props.onChangeEnd()

        Helpers.stopEvent(it)
        false
    }
    //endregion

    //region right border
    private fun onRightDragStart(e: Event) : Boolean{
        var coords = Helpers.getEventCoords(e)
        dragStartX = coords[0]
        dragStartWidth = currentWidth

        document.body?.onmousemove = {onRightDragMove(it)}
        document.body?.asDynamic().ontouchmove =  {event: Event -> onLeftDragMove(event)}

        document.body?.onmouseup = {onRightDragEnd(it)}
        document.body?.asDynamic().ontouchend = {event: Event -> onLeftDragEnd(event)}

        props.onChangeStart()

        Helpers.stopEvent(e)
        return false
    }

    private var onRightDragMove: (e: Event) -> Boolean = {
        var coords = Helpers.getEventCoords(it)
        var d = snapPxToMinutes(coords[0] - dragStartX, props.hourWidth)
        var minWidth = convertMinutesToPx(props.hourWidth, MIN_DURATION)

        currentWidth = kotlin.math.max((this.dragStartWidth + d), minWidth)

        var date = getDate(currentLeft,props.hourWidth,props.timeLineStart)
        var duration = convertPxToMinutes(props.hourWidth, currentWidth)

        el.setStyles("width:${currentWidth}px;left:${currentLeft}px;")
        props.onChange(date, duration)

        Helpers.stopEvent(it)
        false
    }

    private var onRightDragEnd: (e: Event) -> Boolean = {
//        var coords = Helpers.getEventCoords(it)
        setState {  }

        document.body?.onmousemove = null
        document.body?.asDynamic().ontouchmove =  null

        document.body?.onmouseup = null
        document.body?.asDynamic().ontouchend = null

        props.onChangeEnd()

        Helpers.stopEvent(it)
        false
    }
    //endregion

    fun setStart(start: Date) {
        this.currentLeft = getLeft(start).toInt()

        el.setStyles("width:${currentWidth}px;left:${currentLeft}px;")
        setState {  }
    }

    override fun componentWillReceiveProps(nextProps: Props) {
        this.currentLeft = FreeBusyTable.snapPxToMinutes(getLeft(props.start).toInt(), props.hourWidth)
        this.currentWidth = FreeBusyTable.snapPxToMinutes(getLeft(props.start.addMinutes(props.duration)).toInt(), props.hourWidth) - currentLeft
    }

    override fun componentWillMount() {
        this.currentLeft = FreeBusyTable.snapPxToMinutes(getLeft(props.start).toInt(), props.hourWidth)
        this.currentWidth = FreeBusyTable.snapPxToMinutes(getLeft(props.start.addMinutes(props.duration)).toInt(), props.hourWidth) - currentLeft

        this.props.onMount(this)
    }

    override fun RBuilder.render() {
        div ("fb-table__dur-bar"){
            attrs.jsStyle {
                width = "${currentWidth}px"
                left = "${currentLeft}px"
            }

            ref {
                this@DurationBar.el = it
            }

            div("fb-table__dur-bar__left-border") {
                ref { this@DurationBar.leftBorderEl = it }

                attrs {
                    onTouchStartFunction = { this@DurationBar.onLeftDragStart(it)}
                    onMouseDownFunction = { this@DurationBar.onLeftDragStart(it) }
                }
            }
            div("fb-table__dur-bar__right-border") {
                ref { this@DurationBar.rightBorderEl = it }

                attrs {
                    onTouchStartFunction = {this@DurationBar.onRightDragStart(it)}
                    onMouseDownFunction = {this@DurationBar.onRightDragStart(it)}
                }
            }
        }
    }
}

fun RBuilder.DurationBar(start: Date,
                         duration: Int,
                         timeLineStart: Date,
                         hourWidth: Int,
                         onChangeStart: () -> Unit,
                         onChange: (Date, Int) -> Unit,
                         onChangeEnd: () -> Unit,
                         onMount: (DurationBar) -> Unit) = child(DurationBar::class) {

    this.attrs.start = start
    this.attrs.duration = duration

    this.attrs.timeLineStart = timeLineStart
    this.attrs.hourWidth = hourWidth
    this.attrs.onMount = onMount

    this.attrs.onChangeStart = onChangeStart
    this.attrs.onChange = onChange
    this.attrs.onChangeEnd = onChangeEnd
}

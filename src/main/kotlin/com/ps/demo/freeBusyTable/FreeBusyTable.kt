package com.ps.kotlinjs.demo.freeBusyTable

import com.ps.demo.helpers.DateHelper
import com.ps.kotlinjs.basekit.Scrollable
import com.ps.kotlinjs.basekit.decorators.*
import com.ps.kotlinjs.basekit.helpers.Cls
import com.ps.kotlinjs.basekit.components.scrollable.ScrollableGroup
import com.ps.kotlinjs.basekit.types.Duration
import com.ps.kotlinjs.basekit.types.Timeout
import com.ps.kotlinjs.basekit.utils.BinarySearch
import com.ps.kotlinjs.demo.models.BusySlot
import com.ps.kotlinjs.demo.models.ReservationSlot
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseUpFunction
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.RDOMBuilder
import react.dom.div
import react.dom.jsStyle
import kotlin.dom.addClass
import kotlin.dom.removeClass
import kotlin.js.Date
import kotlin.math.absoluteValue

class FreeBusyTable: RComponent<FreeBusyTable.Props, RState>() {
    interface Props : RProps {
        var hourWidth: Int
        var timeLineStart: Date
        var timeLineEnd: Date
        var groups : List<FreeBusyTable.Group>
        var busySlots: List<BusySlot>
        var onTimeChange : (Date, Int) -> Unit
        var start: Date
        var duration: Int
    }

    // if styles are changed,adjust these values
    private val HEADER_HEIGHT = 30
    private val ROW_HEIGHT = 40

    private var scrollBinding = ScrollableGroup()
    private var hideLabelsTimeout: Timeout? = null
    private var scrollableEventsCol : Scrollable? = null
    private var durationBar: DurationBar? = null

    private var pinnedHeader: Element? = null
    private var leftTimeEl : Element? = null
    private var rightTimeEl : Element? = null

    private var headersIndices : MutableList<Int> = mutableListOf()
    private var headersInnerHTML : MutableList<String> = mutableListOf()
    private var pinnedHeaderY = -1


    private lateinit var start: Date
    private var duration: Int = -1

    init {

    }

    // region handling of time labels when scrolling
    private fun onTimeElsMounted(left: Element, right: Element) {
        leftTimeEl = left
        rightTimeEl = right
    }

    private fun onTimeChangeStart(start: Date = this.start,
                                  duration: Int = this.duration) {
        onTimeChange(start, duration)

        leftTimeEl?.addClass("--showen")
        rightTimeEl?.addClass("--showen")

        hideLabelsTimeout?.stop()
    }

    private fun onTimeChange(start: Date, dur: Int) {
        this.start = start
        this.duration = dur

        val end = start.addMinutes(dur)

        val lLeft = getLeft(start, props.hourWidth, props.timeLineStart)
        val lRight = getLeft(end, props.hourWidth, props.timeLineStart)

        leftTimeEl?.setStyles("left:${lLeft}px")
        rightTimeEl?.setStyles("left:${lRight}px")

        leftTimeEl?.innerHTML = DateHelper.getClockTime(start)
        rightTimeEl?.innerHTML = DateHelper.getClockTime(end)

        props.onTimeChange(start, dur)
    }

    private fun onTimeChangeEnd() {
        leftTimeEl?.removeClass("--showen")
        rightTimeEl?.removeClass("--showen")
    }

    // endregion

    private val MOUSE_DOWN_CLICK_TIME_LIMIT = 190
    private val MOUSE_DOWN_CLICK_D = 5
    private lateinit var mouseDownTime: Date
    private var mouseDownX: Int = 0

    private fun onTimeLineMouseDown(e: Event) {
        mouseDownTime = Date()
        mouseDownX = e.asDynamic().nativeEvent.clientX as Int
    }

    private fun onTimeLineMouseUp(e: Event) {
        var mouseUpX = e.asDynamic().nativeEvent.clientX as Int

        if(Date() - mouseDownTime > MOUSE_DOWN_CLICK_TIME_LIMIT) return
        if(kotlin.math.abs(mouseDownX - mouseUpX) > MOUSE_DOWN_CLICK_D) return

        var left = e.asDynamic().nativeEvent.clientX - scrollableEventsCol!!.scrollContent.getBoundingClientRect().left
        var snappedLeft = FreeBusyTable.snapPxToMinutes(left, props.hourWidth)

        var date = getDate(snappedLeft, props.hourWidth, props.timeLineStart)

        durationBar!!.setStart(date)

        onTimeChangeStart(date, duration)

        hideLabelsTimeout?.stop()
        hideLabelsTimeout = Timeout({
            this.onTimeChangeEnd()
        }, 1500)
    }

    private var onScroll : (Int, Int) -> Unit = fn@{ x, y ->
        if(pinnedHeader == null) return@fn

        var closestHeaderI = BinarySearch.nearestValue(headersIndices, y + HEADER_HEIGHT)
        var closestHeader = headersIndices[closestHeaderI]
        var ty = y + HEADER_HEIGHT - closestHeader
        var html: String

        if(ty.absoluteValue > HEADER_HEIGHT) {
            ty = 0

            html = headersInnerHTML[closestHeaderI]
        } else {
            val i = kotlin.math.max(closestHeaderI - 1, 0)
            html = headersInnerHTML[i]
        }

        ty = -ty

        if(pinnedHeader?.innerHTML != html) {
            pinnedHeader?.innerHTML = html
        }

        if(ty != pinnedHeaderY) {
            pinnedHeader?.translate(0, ty)
            pinnedHeaderY = ty
        }
    }

    override fun componentWillMount() {
        this.start = props.start
        this.duration = props.duration
    }

    override fun componentWillReceiveProps(nextProps: Props) {
        this.start = nextProps.start
        this.duration = nextProps.duration
    }

    override fun RBuilder.render() {
        var timeLineWidth = getLeft(props.timeLineEnd, props.hourWidth, props.timeLineStart)

        div("fb-table") {
            div("fb-table__labels-col") {
                div("fb-table__labels-col__header pinned") {
                    ref {
                        pinnedHeader = it
                    }

                    attrs.jsStyle {
                        transform = "translateX(-1000px)"
                    }

                    + props.groups.first().header
                }

                Scrollable(Scrollable.Direction.VERTICAL,
                        null,
                        {
                            it.addOnScrollObserver(onScroll)
                            scrollBinding.add(it, Scrollable.Direction.VERTICAL)
                        },
                        {
                            it.removeOnScrollObserver(onScroll)
                            scrollBinding.remove(it)
                        }) {


                    var headerTop = 0
                    headersInnerHTML.clear()
                    headersIndices.clear()

                    props.groups.forEach { group ->
                        headersIndices.add(headerTop)
                        headersInnerHTML.add(group.header)

                        div("fb-table__labels-col__header") {
                            + group.header
                        }

                        headerTop += HEADER_HEIGHT


                        group.rows.forEach {
                            div("fb-table__labels-col__label") {
                                it.label.invoke(this)
                            }

                            headerTop += ROW_HEIGHT
                        }
                    }
                }
            }

            TimeTemplate(props.timeLineStart,
                    props.timeLineEnd,
                    props.hourWidth,
                    props.busySlots,
                    {scrollBinding.add(it, Scrollable.Direction.HORIZONTAL)},
                    {scrollBinding.remove(it)},
                    {l,r -> onTimeElsMounted(l,r)})

            div("fb-table__events-col") {
                Scrollable(Scrollable.Direction.BOTH,
                        Scrollable.Direction.BOTH,
                        {
                            it.addOnScrollObserver(onScroll)
                            scrollBinding.add(it, Scrollable.Direction.BOTH)
                            scrollableEventsCol = it
                        },
                        {
                            it.removeOnScrollObserver(onScroll)
                            scrollBinding.remove(it)
                        }) {

                    attrs.onMouseDownFunction = { onTimeLineMouseDown(it) }
                    attrs.onMouseUpFunction = { onTimeLineMouseUp(it) }

                    props.groups.forEach {
                        div("fb-table__events-col__header") {
                            attrs.jsStyle {
                                width = timeLineWidth
                            }
                        }

                        it.rows.forEach { row ->
                            div("fb-table__events-col__row") {
                                attrs.jsStyle {
                                    width = timeLineWidth
                                }

                                row.reservationSlots.forEach {
                                    div(Cls("fb-table__events-col__row__event")
                                            .add(it.isFree, "--free", "--busy")
                                            .toString()) {

                                        attrs {
                                            var l = 1 + getLeft(it.start, props.hourWidth, props.timeLineStart)
                                            var w = -1 + getLeft(it.end, props.hourWidth, props.timeLineStart) - getLeft(it.start, props.hourWidth, props.timeLineStart)

                                            jsStyle {
                                                left = l
                                                width = w
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    DurationBar(start,
                            duration,
                            props.timeLineStart,
                            props.hourWidth,
                            { onTimeChangeStart() },
                            { start, duration -> onTimeChange(start, duration) },
                            { onTimeChangeEnd() },
                            { durationBar = it })

                    CurrentTimeLine(props.hourWidth, props.timeLineStart)
                }
            }

            div("fb-table__recenter-btn") {
                attrs.onClickFunction = {
                    var left = getLeft(start.addHours(-2), props.hourWidth, props.timeLineStart)
                    scrollBinding.scrollTo(left.toInt(), null)
                }

                + "RC"
            }
        }
    }



    companion object {
        fun snapPxToMinutes(value: Int, hourWidth: Int) : Int {
            var minutesSnap = 5
            var snap = (hourWidth / 60 * minutesSnap).toDouble()

            return kotlin.math.round(value / snap).toInt() * snap.toInt()
        }

        fun getLeft(date: Date, hourWidth: Int, timeLineStart: Date) : Double {
            var hoursDiff = Duration(timeLineStart, date).getHours(false)

            return hourWidth * hoursDiff
        }

        fun getDate(left: Int, hourWidth: Int, timeLineStart: Date) : Date {
            return timeLineStart.addMinutes(kotlin.math.round(left.toDouble() / hourWidth * 60).toInt())
        }

        fun convertMinutesToPx(hourWidth: Int, minutes: Int) : Int {
            return kotlin.math.round(60.0 / hourWidth.toDouble() * minutes).toInt()
        }

        fun convertPxToMinutes(hourWidth: Int, px: Int) : Int {
            return kotlin.math.round(60.0 / hourWidth.toDouble() * px).toInt()
        }
    }

    data class Row (
            var label : (RDOMBuilder<DIV>.() -> Unit),
            var reservationSlots : List<ReservationSlot>
    )

    data class Group (
            var header: String,
            var rows: List<Row>
    )
}

fun RBuilder.FreeBusyTable(groups: List<FreeBusyTable.Group>,
                           hourWidth: Int,
                           timeLineStart: Date,
                           timeLineEnd: Date,
                           busySlots: List<BusySlot>,
                           onTimeChange: (Date, Int) -> Unit,
                           start: Date,
                           duration: Int) = child(FreeBusyTable::class) {

    attrs {
        this.hourWidth = hourWidth
        this.groups = groups
        this.busySlots = busySlots

        this.timeLineStart = timeLineStart.clearHour()
        this.timeLineEnd = timeLineEnd.clearHour()
        this.onTimeChange = onTimeChange

        this.start = start
        this.duration = duration
    }
}
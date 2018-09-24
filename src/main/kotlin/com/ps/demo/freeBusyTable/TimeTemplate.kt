package com.ps.kotlinjs.demo.freeBusyTable

import com.ps.demo.helpers.DateHelper
import com.ps.kotlinjs.basekit.Scrollable
import com.ps.kotlinjs.basekit.decorators.addDays
import com.ps.kotlinjs.basekit.decorators.clearTime
import com.ps.kotlinjs.basekit.decorators.compareTo
import com.ps.kotlinjs.basekit.decorators.translate
import com.ps.kotlinjs.basekit.utils.BinarySearch
import com.ps.kotlinjs.demo.models.BusySlot
import org.w3c.dom.Element
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.jsStyle
import kotlin.js.Date
import kotlin.math.absoluteValue

// Snapping of date should be implemented here using onScroll function

class TimeTemplate : RComponent<TimeTemplate.Props, RState>() {
    interface Props : RProps {
        var timeLineStart : Date
        var timeLinEnd: Date
        var hourWidth: Int
        var busySlots: List<BusySlot>


        var onTimeElsMount: (Element, Element) -> Unit
        var onScrollableMount: ((Scrollable) -> Unit)?
        var onScrollableUnmount: ((Scrollable) -> Unit)?
    }

    private var leftTimeEl: Element? = null
    private var rightTimeEl: Element? = null

    private var dateLabelElement : Element? = null
    private var dateLabelWidth = 180
    private var dateLabelX = -1
    private lateinit var dateLabelsIndices : MutableList<Int>
    private lateinit var dateLabelsInnerHTML : MutableList<String>

    private fun onScroll(x: Int, y: Int) {
        if(dateLabelElement == null) return

        var closestHeaderI = BinarySearch.nearestValue(dateLabelsIndices, x + dateLabelWidth)
        var closestHeader = dateLabelsIndices[closestHeaderI]
        var tx = x + dateLabelWidth - closestHeader
        var html: String

        if(tx.absoluteValue > dateLabelWidth) {
            tx = 0
            html = dateLabelsInnerHTML[closestHeaderI]

        } else {
            val i = kotlin.math.max(closestHeaderI - 1, 0)
            html = dateLabelsInnerHTML[i]
        }

        tx = -tx

        if(tx != dateLabelX) {
            if(dateLabelElement?.innerHTML != html) {
                dateLabelElement?.innerHTML = html
            }

            dateLabelElement?.translate(tx)
            dateLabelX = tx
        }
    }

    override fun RBuilder.render() {
        leftTimeEl = null
        rightTimeEl = null

        div ("fb-table__template"){
            div("t-l pinned") {
                attrs.jsStyle {
                    transform = "translateX(-1000px)"
                }
                ref {
                    this@TimeTemplate.dateLabelElement = it
                    this@TimeTemplate.dateLabelWidth = (it as Element?)?.clientWidth ?: 0

                    console.log(this@TimeTemplate.dateLabelElement, this@TimeTemplate.dateLabelWidth)
                }
                
                + "${Date().toISOString()}"
            }
            Scrollable(Scrollable.Direction.HORIZONTAL,
                    null,
                    {
                        // register functions to handle sticky date
                        props.onScrollableMount?.invoke(it)
                        it.addOnScrollObserver({x,y -> this@TimeTemplate.onScroll(x,y)}, false)
                    },
                    {
                        // unregister functions to handle sticky date
                        props.onScrollableUnmount?.invoke(it)
                    }) {

                div("fb-table__template__time-label --left") {
                    ref {
                        this@TimeTemplate.leftTimeEl = it
                        if(leftTimeEl != null && rightTimeEl != null) props.onTimeElsMount(leftTimeEl!!, rightTimeEl!!)
                    }
                }

                div("fb-table__template__time-label --right") {
                    ref {
                        this@TimeTemplate.rightTimeEl = it
                        if(leftTimeEl != null && rightTimeEl != null) props.onTimeElsMount(leftTimeEl!!, rightTimeEl!!)
                    }
                }

                props.busySlots.forEach {
                    div("fb-table__template__busy-slot") {
                        attrs.jsStyle {
                            left = "${FreeBusyTable.getLeft(it.start, props.hourWidth, props.timeLineStart)}px"
                            width = "${FreeBusyTable.getLeft(it.end, props.hourWidth, it.start)}px"
                        }
                    }
                }

                var dateLabelsInnerHTML= mutableListOf<String>()
                var dateIndices = mutableListOf<Int>(0)
                var day = props.timeLineStart.clearTime()

                while(day <= props.timeLinEnd) {
                    div("t-day") {
                        div("t-l") {
                            dateLabelsInnerHTML.add(day.toISOString())
                            +"${day.toISOString()}"
                        }

                        var hour = if(day.getTime() == props.timeLineStart.clearTime().getTime()) props.timeLineStart.getHours() else 0
                        var maxHour = if(day.getTime() == props.timeLinEnd.clearTime().getTime()) props.timeLinEnd.getHours() else 23

                        dateIndices.add(dateIndices.last() + (maxHour + 1 - hour) * props.hourWidth)

                        while(hour <= maxHour) {
                            div("t-h") {
                                attrs.jsStyle {
                                    width = props.hourWidth / 2
                                }

                                div("t-h__time") {
                                    +"${DateHelper.zeroize(hour)}:00"
                                }
                            }
                            div("t-hh") {
                                attrs.jsStyle {
                                    width = props.hourWidth / 2
                                }
                            }

                            hour++
                        }
                    }

                    day = day.addDays(1)
                }

                this@TimeTemplate.dateLabelsIndices = dateIndices
                this@TimeTemplate.dateLabelsInnerHTML = dateLabelsInnerHTML
            }
        }
    }
}

fun RBuilder.TimeTemplate(timeLineStart: Date,
                          timeLineEnd: Date,
                          hourWidth: Int,
                          busySlots: List<BusySlot>,
                          onScrollableMount: ((Scrollable) -> Unit)?,
                          onScrollableUnmount: ((Scrollable) -> Unit)?,
                          onTimeElsMount: (Element, Element) -> Unit) = child(TimeTemplate::class) {

    this.attrs.onScrollableMount = onScrollableMount
    this.attrs.onScrollableUnmount = onScrollableUnmount

    this.attrs.hourWidth = hourWidth
    this.attrs.timeLineStart = timeLineStart
    this.attrs.timeLinEnd = timeLineEnd
    this.attrs.busySlots = busySlots

    this.attrs.onTimeElsMount = onTimeElsMount
}

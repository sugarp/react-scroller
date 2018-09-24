package com.soc.meeton.website

import com.ps.demo.DataMocker
import com.ps.kotlinjs.basekit.Application
import com.ps.kotlinjs.basekit.decorators.addDays
import com.ps.kotlinjs.basekit.decorators.clearHour
import com.ps.kotlinjs.basekit.decorators.clearTime
import com.ps.kotlinjs.demo.freeBusyTable.FreeBusyTable
import com.ps.kotlinjs.demo.models.ReservationSlot
import react.dom.render
import kotlin.browser.document
import kotlin.js.Date
import kotlin.js.Math

class DemoApp : Application() {
    private val START = Date().clearTime().getTime()
    private val END = Date().clearTime().addDays(4).getTime()

    override fun launch() {

    }

    override fun onDOMReady() {
        console.log(START, END)
        render(document.getElementById("root")) {
            FreeBusyTable(getData(),
                    80,
                    Date(START),
                    Date(END),
                    emptyList(),
                    {date, duration -> {
//                        console.log(date, duration)
                    }},
                    Date().clearHour(),
                    60)
        }
    }

    private fun getData() : List<FreeBusyTable.Group> {
        val groups = mutableListOf<FreeBusyTable.Group>()

        for(i in 0..randomBetween(20,30)) {
            groups.add(getGroup())
        }

        return groups
    }

    private fun getGroup() : FreeBusyTable.Group {
        val name = randomBetween(1000, 5000).toString()
        val rows = mutableListOf<FreeBusyTable.Row>()

        for(i in 0..randomBetween(5,15)) {
            rows.add(getRow())
        }

        return FreeBusyTable.Group(name, rows)
    }

    private fun getRow(): FreeBusyTable.Row {
        val slots = mutableListOf<ReservationSlot>()

        prevEnd = null
        for(i in 0..randomBetween(10,20)) {
            slots.add(getTimeSlot())
        }

        slots.sortBy { it.start.getTime() }

        return FreeBusyTable.Row({
            + "Name:${Math.round(Math.random())}"
        }, slots)
    }

    private var prevEnd : Date? = null
    private fun getTimeSlot() : ReservationSlot {
        val slot =  ReservationSlot()

        slot.start = Date(randomBetween((prevEnd?.getTime() ?: START).toLong(), END.toLong()))
        slot.end = Date(randomBetween(slot.start.getTime().toLong(), END.toLong()))

        prevEnd = slot.end

        return slot
    }

    private fun randomBetween(min: Long, max: Long) : Int {
        return Math.floor(Math.random() * (max-min+1) + min)
    }
}
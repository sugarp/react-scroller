package com.ps.kotlinjs.demo.models

import kotlin.js.Date

open class ReservationSlot {
        lateinit var start: Date
        lateinit var end: Date
        lateinit var subject: String
        var isFree: Boolean = false
    }
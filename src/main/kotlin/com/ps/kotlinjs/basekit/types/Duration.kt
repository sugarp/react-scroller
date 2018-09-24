package com.ps.kotlinjs.basekit.types

import com.ps.kotlinjs.basekit.decorators.minus
import kotlin.math.*
import kotlin.js.Date

class Duration(private var from : Date, private var to: Date) {
    fun getHours(abs : Boolean = true) : Double {
        return getMinutes(abs) / 60.0
    }

    fun getMinutes(abs : Boolean = true) : Int {
        return if(abs) {
            abs((to - from) / 1000 / 60)
        } else {
            (to - from) / 1000 / 60
        }
    }

    fun getMilli(abs : Boolean = true) : Int {
        return if(abs) {
            abs(to - from)
        } else {
            to - from
        }
    }
}
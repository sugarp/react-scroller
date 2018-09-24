package com.ps.kotlinjs.basekit.types

import kotlin.browser.window

class Timeout {
    var timer : Int = -1
    var fn : () -> Unit
    var ms : Int
    var isStarted = false

    constructor(fn: () -> Unit, ms: Int) {
        this.ms = ms
        this.fn = fn

        start()
    }

    fun stop() {
        window.clearTimeout(timer)
        isStarted = false
    }

    fun start() {
        if(!isStarted) {
            timer = window.setTimeout({
                fn.invoke()
                isStarted = false
            }, ms)
            isStarted = true
        }
    }

    fun reset() {
        stop()
        start()
    }
}


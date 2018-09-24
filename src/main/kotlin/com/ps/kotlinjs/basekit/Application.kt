package com.ps.kotlinjs.basekit

import kotlin.browser.document
import kotlin.browser.window

abstract class Application {
    companion object {
        fun isAndroidJS(): Boolean {
            var window = window.asDynamic()

            return window.cordova && window.cordova.platformId == "android"
        }

        fun isElectron(): Boolean {
            var window = window.asDynamic()

            return !window.cordova
        }
    }

    constructor() {
        window.asDynamic()["app"] = this
        beforeLaunch()
        launch()

        if (document.body != null) {
            onDOMReady()
        } else {
            document.addEventListener("DOMContentLoaded", { onDOMReady() })
        }
    }

    //TODO: check performance leaks
    fun reRender() {
        window.location.reload()
    }

    open fun beforeLaunch() {

    }

    abstract fun launch()

    abstract fun onDOMReady()
}
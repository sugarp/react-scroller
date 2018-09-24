package com.ps.kotlinjs.demo.freeBusyTable

import com.ps.demo.helpers.DateHelper
import com.ps.kotlinjs.basekit.types.Timeout
import kotlinx.html.DIV
import react.*
import react.dom.RDOMBuilder
import react.dom.div
import react.dom.jsStyle
import kotlin.js.Date

class CurrentTimeLine : RComponent<CurrentTimeLine.Props, CurrentTimeLine.State>() {
    interface Props : RProps {
        var hourWidth: Int
        var timeLineStart: Date
    }

    interface State : RState {
        var currentDateTime: Date
    }

    override fun State.init() {
        this.currentDateTime = Date()
    }

    private var timeout : Timeout? = null

    private fun setTimeout() {
        var now = Date()
//        var ms = (60 - now.getSeconds()) * 1000
        var ms = (60 - now.getSeconds()) * 100

        timeout = Timeout({
            setState { currentDateTime = Date() }
            setTimeout()
        }, ms)
    }

    override fun componentDidMount() {
        setTimeout()
    }

    override fun componentWillUnmount() {
        timeout?.stop()
    }

    override fun RBuilder.render() {
        div ("fb-table__cur-time"){
            attrs.jsStyle {
                left = "${FreeBusyTable.getLeft(state.currentDateTime, props.hourWidth, props.timeLineStart)}px"
            }

            div("fb-table__cur-time__clock") {
                + DateHelper.getClockTime(state.currentDateTime)
            }
        }
    }
}

fun RBuilder.CurrentTimeLine(hourWidth: Int,
                             timeLineStart: Date) = child(CurrentTimeLine::class) {

    attrs {
        this.hourWidth = hourWidth
        this.timeLineStart = timeLineStart
    }
}

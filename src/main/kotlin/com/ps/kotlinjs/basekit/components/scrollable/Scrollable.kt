package com.ps.kotlinjs.basekit

import com.ps.kotlinjs.basekit.components.scrollable.Scrolling
import com.ps.kotlinjs.basekit.components.scrollable.Indicator
import com.ps.kotlinjs.basekit.decorators.setStyles

import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

/*
*   This is designed as stateless component in order to make it as efficient as possible.
*   Scroll position can't be changed using props. Re-rendering would be way too expensive.
*
*   You can define "onMount" and "onUnmout" functions where you get this class as argument.
*   Later you can scroll programmatically or listen to scroll events using addOnScrollObserver.
* */

class Scrollable: RComponent<Scrollable.Props, RState>() {
    interface Props : RProps {
        var cls : String
        var direction : Direction
        var indicator: Direction?

        var onMount: ((Scrollable) -> Unit)?
        var onUnmount: ((Scrollable) -> Unit)?

        var content : (RDOMBuilder<DIV>.() -> Unit)
    }

    private var onScrollObservers : dynamic = js("[]")
    private var onScrollSuspendableObservers : dynamic = js("[]")

    private var scrolling : Scrolling? = null
    private var scrollInterval : Int = -1
    private var scrollTimeout : Int = -1
    private var easingIntercepted: Boolean = false


    private var easingStartPos: List<Int> = listOf()
    private var dragStartScroll: List<Int> = listOf()
    private var touchStartPos: List<Int> = listOf()
    private var lastMove: List<Int>? = null

    private var xAxis = true
    private var yAxis = true
    
    private var indicatorXEl : Element? = null
    private var indicatorYEl : Element? = null
    private var indicatorX : Indicator? = null
    private var indicatorY : Indicator? = null

    private var minScrollX = 0
    private var minScrollY = 0
    // these are calculated when styles are applied and dom is mounted
    private var maxScrollX = 0
    private var maxScrollY = 0

    lateinit var scrollView : Element
    lateinit var scrollContent : Element

    var scrollPositionX = 0
    var scrollPositionY = 0

    fun addOnScrollObserver(fn: ((Int, Int) -> Unit), suspendable: Boolean = true) {
        (if(suspendable) onScrollSuspendableObservers else onScrollObservers).push(fn)
    }

    fun removeOnScrollObserver(fn: ((Int, Int) -> Unit)) {
        var obs = this.onScrollObservers
        var susObs = this.onScrollSuspendableObservers

        js("""
            var index = susObs.indexOf(fn);
            if (index > -1) {
              susObs.splice(index, 1);
            } else {
                var index = obs.indexOf(fn);
                if (index > -1) {
                  obs.splice(index, 1);
                }
            }
            """)
//
//        var array = [2, 5, 9];
//        var index = array.indexOf(5);
//        if (index > -1) {
//            array.splice(index, 1);
//        }
//        if(!onScrollSuspendableObservers.remove(fn)) {
//            onScrollObservers.remove(fn)
//        }
    }

    fun scrollTo(left: Int? = null,
                 top: Int? = null,
                 suspendOnScroll: Boolean = false,
                 animate: Boolean = false) {

        var x = left ?: scrollPositionX
        var y = top ?: scrollPositionY

        if(x < minScrollX) {
            x = minScrollX
        } else if(x > maxScrollX) {
            x = maxScrollX
        }

        if(y < minScrollY) {
            y = minScrollY
        } else if(y > maxScrollY) {
            y = maxScrollY
        }

//        console.log("scrolling:", x, y)
//        console.log("ration:", x/maxScrollX as Double,y/maxScrollY as Double)


        indicatorX?.setRatio(x/maxScrollX as Double, animate)
        indicatorY?.setRatio(y/maxScrollY as Double, animate)

        scrollPositionX = x
        scrollPositionY = y

        var styles = if(animate) {
            "-webkit-transform:translate3d(${-scrollPositionX}px,${-scrollPositionY}px,0);${Scrollable.scrollToTransition}"
        } else {
            "-webkit-transform:translate3d(${-scrollPositionX}px,${-scrollPositionY}px,0);"
        }

        scrollContent.setStyles(styles)

//        scrollContent.asDynamic().style.webkitTransform = "translate3d(${-scrollPositionX}px,${-scrollPositionY}px,5px)"

        if(!suspendOnScroll && onScrollSuspendableObservers.length > 0) {
            var susObs = onScrollSuspendableObservers

            js("""
                for(var i = 0; i < susObs.length; i++) {
                    susObs[i](x,y)
                }
                """)
//            onScrollSuspendableObservers?.forEach { it.invoke(x,y) }
        }

        var obs = onScrollObservers
        js("""
                for(var i = 0; i < obs.length; i++) {
                    obs[i](x,y)
                }
                """)
//        onScrollObservers.forEach { it.invoke(x,y) }
    }

    fun scrollToRatio(ratioX: Double?, ratioY: Double?) {
        val x = (if(ratioX != null) maxScrollX * ratioX else scrollPositionX) as Int
        val y = (if(ratioY != null) maxScrollY * ratioY else scrollPositionY) as Int

        scrollTo(x, y)
    }

    //

    override fun RBuilder.render() {
//        console.log("Scrollable: RENDER")

//        div(Cls("scrollView").add(props.cls).build()) {
        div("scroll-view") {
            this.ref { this@Scrollable.scrollView = it }

            if(props.indicator != Direction.NONE) {
                this.attrs.onWheelFunction = { this@Scrollable.onWheel(it); false }
                this.attrs.onTouchStartFunction = { this@Scrollable.onTouchStart(it) }
                this.attrs.onMouseDownFunction = { this@Scrollable.onTouchStart(it) }
            }

            if(props.indicator == Direction.HORIZONTAL || props.indicator == Direction.BOTH) {
                div("scroll-bar x") {
                    div("indicator") {
                        ref { indicatorXEl = it as? Element }
                    }
                }
            }

            if(props.indicator == Direction.VERTICAL || props.indicator == Direction.BOTH) {
                div("scroll-bar y") {
                    div("indicator") {
                        ref { indicatorYEl = it as? Element }
                    }
                }
            }

            div("scroll-content") {
                this.ref{this@Scrollable.scrollContent = it as Element }
                this.attrs.jsStyle {
                    transform = "translate3d(${-scrollPositionX}px,${-scrollPositionY}px),0"
                }

                // last line of lambda fn is returned
                props.content.invoke(this)
            }
        }
    }

    override fun componentWillReceiveProps(nextProps: Props) {
        this.xAxis = nextProps.direction == Direction.HORIZONTAL || nextProps.direction == Direction.BOTH
        this.yAxis = nextProps.direction == Direction.VERTICAL || nextProps.direction == Direction.BOTH
    }

    override fun componentDidMount() {
        setup()
        window.addEventListener("resize", onWindowResize)
        props.onMount?.invoke(this)
    }

    override fun componentWillUnmount() {
        window.removeEventListener("resize", onWindowResize)
        props.onUnmount?.invoke(this)
    }

    //

    private fun setup() {
        maxScrollX = kotlin.math.max(scrollContent.clientWidth - scrollView.clientWidth, 0)
        maxScrollY = kotlin.math.max(scrollContent.clientHeight - scrollView.clientHeight, 0)

        if(indicatorXEl != null) {
            indicatorX = Indicator(indicatorXEl as Element,
                    Indicator.Direction.horizontal,
                    this,
                    scrollView.clientWidth,
                    scrollContent.clientWidth)
        }

        if(indicatorYEl != null) {
            indicatorY = Indicator(indicatorYEl as Element,
                    Indicator.Direction.vertical,
                    this,
                    scrollView.clientHeight,
                    scrollContent.clientHeight)
        }
    }

    private val onWindowResize: (e: Event) -> Unit = {
        setup()
        scrollTo(null, null, true, false)
    }

    private fun onWheel(event: Event) {
        interceptEasing()

        // X scrolling is too fast, lets slow it
        val scrollX = if(xAxis) event.asDynamic()["deltaX"] as Int / 5 else 0
        val scrollY = if(yAxis) event.asDynamic()["deltaY"] as Int else 0

//        console.log("onWheel", scrollX, scrollY)
        this.scrollTo(scrollPositionX + scrollX, scrollPositionY + scrollY)

        event.preventDefault()
    }

    private fun onTouchStart(event: Event) {
//        console.log("touchstart", event.asDynamic().type, event.asDynamic().clientX, event.asDynamic().clientY)

        interceptEasing()

        var xy = Helpers.getEventCoords(event)

        touchStartPos = xy
        dragStartScroll = listOf(scrollPositionX, scrollPositionY)

        scrolling = Scrolling(Date().getTime() as Int, xy[0], xy[1])

        lastMove = null

        document.body?.onmousemove = {onTouchMove(it)}
        document.body?.asDynamic().ontouchmove =  {event: Event -> onTouchMove(event)}

        document.body?.onmouseup = {onTouchEnd(it)}
        document.body?.asDynamic().ontouchend = {event: Event -> onTouchEnd(event)}
    }

    private fun onTouchMove(event: Event) {
        var xy = Helpers.getEventCoords(event)
//
//        console.log("touchmove", xy[0], xy[1])
//        console.log(dragStartScroll[0], (dragStartScroll[1]))

        var x = if(xAxis) dragStartScroll[0] - (xy[0] - touchStartPos[0]) else 0
        var y = if(yAxis) dragStartScroll[1] - (xy[1] - touchStartPos[1]) else 0

        this.lastMove = xy
        this.scrollTo(x, y)
    }

    private fun onTouchEnd(event: Event) {
//        console.log("touchend", event.asDynamic().type, event.asDynamic().clientX, event.asDynamic().clientY)

        document.body?.onmousemove = null
        document.body?.asDynamic().ontouchmove =  null

        document.body?.onmouseup = null
        document.body?.asDynamic().ontouchend = null

        if(lastMove != null) {
            scrolling!!.finalize(Date().getTime() as Int, lastMove!![0], lastMove!![1])
            invokeScrollEasing(this.scrolling!!)
        }
    }

    private fun invokeScrollEasing(scrolling: Scrolling) {
        val t = scrolling.getDuration().toInt()
        val endT = Date().getTime() + t

        window.clearTimeout(this.scrollTimeout)
        window.clearInterval(this.scrollInterval)

        if(t <= 0) return

        easingIntercepted = false
        easingStartPos = listOf(scrollPositionX, scrollPositionY)

        var onFrame : () -> Unit = {}

        onFrame = {
            if(!easingIntercepted) {

                var ft = Date()
                val xy = this.scrolling!!.getPositionInTime(ft)
                val sx = (easingStartPos[0] - xy[0]).toInt()
                val sy = (easingStartPos[1] - xy[1]).toInt()

                if ((sx <= minScrollX || sx >= maxScrollX) &&
                        (sy <= minScrollY || sy >= maxScrollY)) {
                    easingIntercepted = true
                }

                this@Scrollable.scrollTo(sx, sy)

                if (ft.getTime() < endT) window.requestAnimationFrame { onFrame() }
            }
        }

        window.requestAnimationFrame{ onFrame() }
    }

    private fun interceptEasing() {
        easingIntercepted = true
    }

    companion object {
        var scrollToTransition = "transition:transform ease-in-out 350ms;"
    }

    enum class Direction {
        NONE,   // NONE makes component scrollable only using "scrollTo(x,y)" function
        BOTH,
        VERTICAL,
        HORIZONTAL
    }
}

fun RBuilder.Scrollable(direction: Scrollable.Direction = Scrollable.Direction.BOTH,
                        indicators: Scrollable.Direction? = Scrollable.Direction.BOTH,
                        onMount: ((Scrollable) -> Unit)? = null,
                        onUnmount: ((Scrollable) -> Unit)? = null,
//                        onScroll: ((Int, Int) -> Unit)? = null,
                        content: RDOMBuilder<DIV>.() -> Unit) = child(Scrollable::class) {

//    this.attrs.cls = cls
    this.attrs.onMount = onMount
    this.attrs.onUnmount = onUnmount
//    this.attrs.onScroll = onScroll
    this.attrs.direction = direction
    this.attrs.content = content
    this.attrs.indicator = indicators
}

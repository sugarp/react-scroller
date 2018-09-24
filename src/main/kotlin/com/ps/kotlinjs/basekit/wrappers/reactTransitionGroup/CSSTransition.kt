package com.ps.kotlinjs.basekit.wrappers.reactTransitionGroup

import org.w3c.dom.Element
import react.RClass
import kotlin.dom.addClass
import kotlin.dom.removeClass

@JsModule("react-transition-group/CSSTransition")
external val CSSTransition: RClass<CSSTransitionProps>

external interface CSSTransitionProps : TransitionProps{
    /**
     * The animation classNames applied to the component as it enters, exits or has finished the transition.
     * A single name can be provided and it will be suffixed for each stage: e.g.
     *
     * `classNames="fade"` applies `fade-enter`, `fade-enter-active`, `fade-enter-done`,
     * `fade-exit`, `fade-exit-active`, `fade-exit-done`, `fade-appear`, and `fade-appear-active`.
     * Each individual classNames can also be specified independently like:
     *
     * ```js
     * classNames={{
     *  appear: 'my-appear',
     *  appearActive: 'my-active-appear',
     *  enter: 'my-enter',
     *  enterActive: 'my-active-enter',
     *  enterDone: 'my-done-enter,
     *  exit: 'my-exit',
     *  exitActive: 'my-active-exit',
     *  exitDone: 'my-done-exit,
     * }}
     * ```
     *
     * @type {string | {
     *  appear?: string,
     *  appearActive?: string,
     *  enter?: string,
     *  enterActive?: string,
     *  enterDone?: string,
     *  exit?: string,
     *  exitActive?: string,
     *  exitDone?: string,
     * }}
     */
    var classNames: String
}

fun CSSTransitionProps.getExitedFn() : (el: Element) -> Unit {
    return {it.addClass("--hidden")}
}

fun CSSTransitionProps.getEnterFn() : (el: Element, appearing: Boolean) -> Unit {
    return {el: Element, appearing: Boolean -> el.removeClass("--hidden")}
}
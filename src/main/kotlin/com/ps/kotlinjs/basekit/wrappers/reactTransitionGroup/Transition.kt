package com.ps.kotlinjs.basekit.wrappers.reactTransitionGroup

import react.RClass
import react.RProps
import org.w3c.dom.Element

@JsModule("react-transition-group/Transition")
external val _transitionImport : dynamic

val Transition: RClass<TransitionProps> = _transitionImport.default

external interface TransitionProps : RProps {
    /**
     * A `function` child can be used instead of a React element.
     * This function is called with the current transition status
     * ('entering', 'entered', 'exiting', 'exited', 'unmounted'), which can be used
     * to apply context specific props to a component.
     *
     * ```jsx
     * <Transition timeout={150}>
     *   {(status) => (
     *     <MyComponent className={`fade fade-${status}`} />
     *   )}
     * </Transition>
     * ```
     */

    //(animationState: String) -> Unit or ReactElement :
    var children: Any
    /**
     * Show the component; triggers the enter or exit states
     */
    // in or out -> hide or hide
    @JsName("in")
    var showen: Boolean

    /**
     * By default the child component is mounted immediately along with
     * the parent `Transition` component. If you want to "lazy mount" the component on the
     * first `in={true}` you can set `mountOnEnter`. After the first enter transition the component will stay
     * mounted, even on "exited", unless you also specify `unmountOnExit`.
     */
    var mountOnEnter : Boolean

    /**
     * By default the child component stays mounted after it reaches the `'exited'` state.
     * Set `unmountOnExit` if you'd prefer to unmount the component after it finishes exiting.
     */
    var unmountOnExit: Boolean

    /**
     * Normally a component is not transitioned if it is shown when the `<Transition>` component mounts.
     * If you want to transition on the first mount set `appear` to `true`, and the
     * component will transition in as soon as the `<Transition>` mounts.
     *
     * > Note: there are no specific "appear" states. `appear` only adds an additional `enter` transition.
     */
    var appear: Boolean

    /**
     * Enable or disable enter transitions.
     */
    var enter: Boolean

    /**
     * Enable or disable exit transitions.
     */
    var exit: Boolean

    /**
     * The duration of the transition, in milliseconds.
     * Required unless `addEndListener` is provided
     *
     * You may specify a single timeout for all transitions like: `timeout={500}`,
     * or individually like:
     *
     * ```jsx
     * timeout={{
     *  enter: 300,
     *  exit: 500,
     * }}
     * ```
     *
     * @type {number | { enter?: number, exit?: number }}
     */
    var timeout: Int

    /**
     * Add a custom transition timeLinEnd trigger. Called with the transitioning
     * DOM node and a `done` callback. Allows for more fine grained transition timeLinEnd
     * logic. **Note:** Timeouts are still used as a fallback if provided.
     *
     * ```jsx
     * addEndListener={(node, done) => {
     *   // use the css transitionend event to mark the finish of a transition
     *   node.addEventListener('transitionend', done, false);
     * }}
     * ```
     */
    var addEndListener: (a : dynamic, b : dynamic) -> Unit

    /**
     * Callback fired before the "entering" status is applied. An extra parameter
     * `isAppearing` is supplied to indicate if the enter stage is occurring on the initial mount
     *
     * @type Function(node: HtmlElement, isAppearing: bool) -> void
     */
    var onEnter: (e: Element, isAppearing: Boolean) -> Unit

    /**
     * Callback fired after the "entering" status is applied. An extra parameter
     * `isAppearing` is supplied to indicate if the enter stage is occurring on the initial mount
     *
     * @type Function(node: HtmlElement, isAppearing: bool)
     */
    var onEntering: (e: Element, isAppearing: Boolean) -> Unit

    /**
     * Callback fired after the "entered" status is applied. An extra parameter
     * `isAppearing` is supplied to indicate if the enter stage is occurring on the initial mount
     *
     * @type Function(node: HtmlElement, isAppearing: bool) -> void
     */
    var onEntered: (e: Element, isAppearing: Boolean) -> Unit

    /**
     * Callback fired before the "exiting" status is applied.
     *
     * @type Function(node: HtmlElement) -> void
     */
    var onExit: (e: Element) -> Unit

    /**
     * Callback fired after the "exiting" status is applied.
     *
     * @type Function(node: HtmlElement) -> void
     */
    var onExiting: (e: Element) -> Unit

    /**
     * Callback fired after the "exited" status is applied.
     *
     * @type Function(node: HtmlElement) -> void
     */
    var onExited: (e: Element) -> Unit
}
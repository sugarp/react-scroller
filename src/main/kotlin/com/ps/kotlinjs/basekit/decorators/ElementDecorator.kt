package com.ps.kotlinjs.basekit.decorators

import org.w3c.dom.Element

fun Element.translate(x: Int = 0, y: Int = 0) {
    var styles = "transform:translate(${x}px,${y}px);"
    this.setAttribute("style", styles)
}

fun Element.setStyles(styles: String) {
    this.setAttribute("style", styles)
}

fun Element.show() {
    this.setAttribute("style", "")
}

fun Element.hide() {
    var styles = "display: none;"
    this.setAttribute("style", styles)
}
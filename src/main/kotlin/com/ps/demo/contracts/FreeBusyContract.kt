package com.ps.kotlinjs.demo.contracts

interface FreeBusyContract {
    class Item {
        var name : String = ""
        var draggable : Boolean = false
    }

    interface Presenter {

    }

    interface View {
        fun setItems(items: List<Item>)
    }
}
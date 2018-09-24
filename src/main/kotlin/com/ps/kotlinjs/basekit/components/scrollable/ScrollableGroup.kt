package com.ps.kotlinjs.basekit.components.scrollable

import com.ps.kotlinjs.basekit.Scrollable

class ScrollableGroup {
    private var both : MutableList<Scrollable> = mutableListOf()
    private var horizontal : MutableList<Scrollable> = mutableListOf()
    private var vertical : MutableList<Scrollable> = mutableListOf()

    // when defined as member, it can be removed from collections of function type
    private var onScroll : (Scrollable, Int?, Int?) -> Unit = {triggerer, x,y ->
        both.forEach { if(triggerer != it) it.scrollTo(x,y, true) }
        vertical.forEach { if(triggerer != it) it.scrollTo(top = y, suspendOnScroll = true) }
        horizontal.forEach { if(triggerer != it) it.scrollTo(left = x, suspendOnScroll = true) }
    }

    fun scrollTo(x:Int? = null,y: Int? = null) {
        both.forEach { it.scrollTo(x,y, true, true) }
        vertical.forEach { it.scrollTo(top = y, suspendOnScroll = true, animate = true) }
        horizontal.forEach { it.scrollTo(left = x, suspendOnScroll = true, animate = true) }
    }

    fun add(scrollable: Scrollable, dir: Scrollable.Direction) {
        when(dir) {
            Scrollable.Direction.BOTH -> both.add(scrollable)
            Scrollable.Direction.HORIZONTAL -> horizontal.add(scrollable)
            Scrollable.Direction.VERTICAL -> vertical.add(scrollable)
        }

        scrollable.addOnScrollObserver({ x, y ->
            val _x = if(dir == Scrollable.Direction.VERTICAL) null else x
            val _y = if(dir == Scrollable.Direction.HORIZONTAL) null else y

            this@ScrollableGroup.onScroll(scrollable, _x,_y)
        })
    }

    fun remove(scrollable: Scrollable) {
        if(!both.remove(scrollable)) {
            if(!horizontal.remove(scrollable)) {
                vertical.remove(scrollable)
            }
        }

        scrollable.removeOnScrollObserver { x, y ->
            this@ScrollableGroup.onScroll(scrollable, x,y)
        }
    }
}

/*
class ScrollableGroup {
    inner class Adapter {
        private var binding: ScrollableGroup

        var scrollable: Scrollable? = null
        var direction: Scrollable.Direction = Scrollable.Direction.BOTH
        var invokeScroll: Boolean = true
        var receiveScroll: Boolean = true

        constructor(b: ScrollableGroup) {
            this.binding = b
        }

        fun onScroll(x: Int, y: Int) {
            binding.onScroll(x,y)
        }
    }

    private var adapters : MutableList<Adapter> = ArrayList()

    fun createAdapter(direction: Scrollable.Direction = Scrollable.Direction.BOTH,
                      invokeScroll: Boolean = true,
                      receiveScroll: Boolean = true) : Adapter {

        var adapter = Adapter(this).apply {
            this.direction = direction
            this.invokeScroll = invokeScroll
            this.receiveScroll = receiveScroll
        }

        adapters.add(adapter)

        return adapter
    }

    fun onScroll(x: Int, y: Int) {
        adapters.forEach {
            when(it.direction) {
                Scrollable.Direction.HORIZONTAL -> it.scrollable?.scrollTo(top = x)
                Scrollable.Direction.VERTICAL -> it.scrollable?.scrollTo(start = y)
                else -> it.scrollable?.scrollTo(x,y)
            }
        }
    }
}*/

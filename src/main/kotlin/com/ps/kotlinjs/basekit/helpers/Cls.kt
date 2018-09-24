package com.ps.kotlinjs.basekit.helpers

class Cls(private var cls : String = "") {

    fun add(cls: String?) : Cls{
        if(cls != null && cls.length > 0) {
            if(cls.length > 0) {
                this.cls += " "
            }

            this.cls += cls
        }

        return this
    }

    fun add(condition: Boolean, cls: String?) : Cls{
        if(!condition) return this

        if(cls != null && cls.length > 0) {
            if(cls.length > 0) {
                this.cls += " "
            }

            this.cls += cls
        }

        return this
    }

    fun add(condition: Boolean, cls: String?, cls2: String?) : Cls{
        if(condition) {
            if (cls != null && cls.length > 0) {
                if (cls.length > 0) {
                    this.cls += " "
                }

                this.cls += cls
            }
        } else {
            if (cls2 != null && cls2.length > 0) {
                if (cls2.length > 0) {
                    this.cls += " "
                }

                this.cls += cls2
            }
        }

        return this
    }

    fun build() : String{
        if (cls.startsWith(" ", true)){
            return cls.substring(1)
        }

        return cls
    }

    override fun toString() : String{
        return cls
    }
}
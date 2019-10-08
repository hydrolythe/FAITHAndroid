package com.divyanshu.draw.widget

data class Point(var x: Float, var y: Float) {

    constructor() : this(0f, 0f)

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
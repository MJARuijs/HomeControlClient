package mjaruijs.homecontrol.colorpicker

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Color(val id: Int, var r: Float, var g: Float, var b: Float) {

    var intValue = 0

    val hue: Float
    val saturation: Float

    init {
        intValue = android.graphics.Color.argb(255, r.toInt(), g.toInt(), b.toInt())

        val max = max(r, max(g, b))
        val min = min(r, min(g, b))

        val delta = max - min

        hue = when {
            delta == 0.0f -> 0.0f
            max == r -> 60.0f * (((g - b) / delta) % 6.0f)
            max == g -> 60.0f * ((b - r) / delta + 2.0f)
            else -> 60.0f * ((r - g) / delta + 4.0f)
        } / 360.0f * 65535.0f

        val lightness = (max + min) / 2.0f

        saturation = when (delta) {
            0.0f -> 0.0f
            else -> delta / (1.0f - abs(2.0f * lightness - 1.0f))
        } * max * 255.0f

    }

    fun r(): Float {
        return r
    }

    fun g(): Float {
        return g
    }

    fun b(): Float {
        return b
    }

    fun getHSL(brightness: Float): Color {
        return Color(0, hue, saturation, brightness)
    }

    override fun toString() = "r=$r, g=$g, b=$b"

}

package mjaruijs.homecontrol.colorpicker

object ColorList {

    val colors = ArrayList<Color>()

    var selection1 = 0
    var selection2 = 0

    init {
        addColor(
                Color(0, 255f, 0f, 0f),
                Color(1, 235f, 20f, 46f),
                Color(2, 156f, 26f, 177f),
                Color(3, 102f, 51f, 185f),
                Color(4, 61f, 77f, 183f),
                Color(5, 16f, 147f, 245f),
                Color(6, 0f, 166f, 246f),
                Color(7, 0f, 187f, 213f),
                Color(8, 0f, 150f, 135f),
                Color(9, 0f, 255f, 0f),
                Color(10, 136f, 196f, 64f),
                Color(11, 205f, 221f, 30f),
                Color(12, 255f, 236f, 22f),
                Color(13, 255f, 192f, 0f),
                Color(14, 255f, 152f, 0f),
                Color(15, 255f, 85f, 5f),
                Color(16, 122f, 85f, 71f),
                Color(17, 157f, 157f, 157f),
                Color(18, 94f, 124f, 139f),
                Color(19, 255f, 255f, 255f)
        )
    }

    private fun addColor(vararg color: Color) {
        colors.addAll(color)
    }

    fun getByID(id: Int): Color {
        for (color in colors) {
            if (color.id == id) {
                return color
            }
        }
        return Color(-1, 0f, 0f, 0f)
    }

    fun getByIntValue(value: Int): Color {
        for (color in colors) {
            if (color.intValue == value) {
                return color
            }
        }
        return Color(-1, 0f, 0f, 0f)
    }
}
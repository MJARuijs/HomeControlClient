package mjaruijs.homecontrol.activities.lampsetup.subcardlist

data class SubCardItem(val name: String, var color: Int = -1) {

    companion object {
        fun parse(string: String): SubCardItem {
            val values = string.split(',')
            val name = values[0]
            val color = values[1].toInt()
            return SubCardItem(name, color)
        }
    }

    override fun toString(): String {
        return "$name,$color"
    }

}
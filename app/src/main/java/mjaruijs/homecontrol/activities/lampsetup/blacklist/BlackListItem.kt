package mjaruijs.homecontrol.activities.lampsetup.blacklist

data class BlackListItem(val name: String) {

    companion object {
        fun parse(string: String): BlackListItem {
            return BlackListItem(string)
        }
    }

    override fun toString(): String {
        return name
    }

}
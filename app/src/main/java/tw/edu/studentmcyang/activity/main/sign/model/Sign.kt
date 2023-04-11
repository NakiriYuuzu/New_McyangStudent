package tw.edu.studentmcyang.activity.main.sign.model

data class Sign(
    val C_Name: String,
    val Sign_id: String,
    val T_Name: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sign

        if (C_Name != other) return false
        if (Sign_id != other) return false
        if (T_Name != other) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Sign_id.hashCode()
        result = 31 * result + C_Name.hashCode()
        result = 31 * result + T_Name.hashCode()
        return result
    }
}
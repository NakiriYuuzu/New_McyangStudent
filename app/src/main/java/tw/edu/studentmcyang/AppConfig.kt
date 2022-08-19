package tw.edu.studentmcyang

object AppConfig {
    const val APP_VERSION = "1.0.0"

    private const val API_URL = "http://192.168.31.74:8081"

    const val API_LOGIN = "$API_URL/api/studentLogin/"
    const val API_SHOW_COURSE = "$API_URL/api/showCourse/"

    // TODO: SharedData Identifier

}
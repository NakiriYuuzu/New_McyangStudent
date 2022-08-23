package tw.edu.studentmcyang

object AppConfig {
    // TODO: APP version
    const val APP_VERSION = "1.0.0"
    const val DEBUG = true

    const val TAG = "=====MCYANG_DEBUG===== : "

    // TODO: App Settings
    const val TIMEOUT = 7

    // TODO: Api URL
    private const val API_URL = "http://192.168.31.74:8081"

    const val API_LOGIN = "$API_URL/api/studentLogin/"
    const val API_LIST_COURSE = "$API_URL/api/showCourse/"
    const val API_LIST_SIGN_COURSE = "$API_URL/api/listSignCourse/"
    const val API_PUSH_SIGN_COURSE = "$API_URL/api/courseSigned/"

    // TODO: SharedData Identifier


    // TODO: Beacon Identifier
    const val BEACON_UUID_SIGN = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"
    const val BEACON_UUID_RACE = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa5"
    const val BEACON_UUID_MAIN = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa4"
    const val BEACON_UUID_ANSWER = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa3"
    const val BEACON_UUID_GROUP = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa2"
    const val BEACON_UUID_TEAM = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa1"
}
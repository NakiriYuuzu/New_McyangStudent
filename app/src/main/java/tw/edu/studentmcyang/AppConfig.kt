package tw.edu.studentmcyang

object AppConfig {
    // TODO: APP version
    const val APP_VERSION = "1.0.0"
    const val DEBUG = true

    const val TAG = "=====MCYANG_DEBUG===== : "

    // TODO: App Settings
    const val TIMEOUT = 7

    // TODO: Api URL
    private const val URL_API = "http://192.168.31.74:8000"

    const val URL_LOGIN = "$URL_API/api/StudentLogin/"
    const val URL_LIST_COURSE = "$URL_API/api/showCourse/"
    const val URL_LIST_SIGN_COURSE = "$URL_API/api/listSignCourse/"
    const val URL_PUSH_SIGN_COURSE = "$URL_API/api/courseSigned/"

    // TODO: Api Param
    const val API_EMAIL = "S_email"
    const val API_PASSWORD = "S_password"

    const val API_SID = "S_id"
    const val API_SNAME = "S_name"


    // TODO: SharedData Identifier


    // TODO: Beacon Identifier
    const val BEACON_UUID_SIGN = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"
    const val BEACON_UUID_RACE = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa5"
    const val BEACON_UUID_MAIN = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa4"
    const val BEACON_UUID_ANSWER = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa3"
    const val BEACON_UUID_GROUP = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa2"
    const val BEACON_UUID_TEAM = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa1"
}
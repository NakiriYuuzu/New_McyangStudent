package tw.edu.studentmcyang

/**
 * 這是設定檔案！
 */
object AppConfig {
    /**
     * 這是 APP 版本
     */
    const val APP_VERSION = "1.0.0"
    const val DEBUG = false

    const val TAG = "=====MCYANG_DEBUG===== : "

    /**
     * 這是 Firebase 環境設定
     */
    const val FIREBASE_URL = "https://newmcyang-default-rtdb.asia-southeast1.firebasedatabase.app/"
    const val FIREBASE_USER = "user"
    const val FIREBASE_MESSAGE = "message"
    const val FIREBASE_DATETIME = "datetime"

    /**
     * 這是 WebSocket 連線網址
     */
    const val WS_RACE = "ws://120.110.115.127/ws/racestudent-server/"
    const val WS_GROUPLIST = "ws://120.110.115.127/ws/group-server/"

    /**
     * 這是 API 網址
     */
    private const val URL_API = "http://120.110.115.127"

    const val URL_LOGINS = "$URL_API/api/Login/"
    const val URL_LIST_COURSE = "$URL_API/api/CourseList/"
    const val URL_LIST_SIGN_COURSE = "$URL_API/api/CourseSignList/"
    const val URL_PUSH_SIGN_COURSE = "$URL_API/api/CourseSignup/"
    const val URL_PUSH_RACE = "$URL_API/api/RaceListCreate/"
    const val URL_LIST_RACE = "$URL_API/api/RaceListList/"
    const val URL_LIST_RACEANSWER = "$URL_API/api/RaceAnswerList/"
    const val URL_LIST_TEAMLEADER = "$URL_API/api/TeamLeaderList/"
    const val URL_CREATE_TEAMLEADER = "$URL_API/api/TeamLeaderCreate/"
    const val URL_CREATE_TEAMMEMBER = "$URL_API/api/TeamMemberCreate/"
    const val URL_LIST_TEAMMEMBER = "$URL_API/api/TeamMemberList/"
    const val URL_LIST_TEAMDESC = "$URL_API/api/TeamDescList/"
    const val URL_LIST_CHATROOM = "$URL_API/api/TeamChatList/"

    /**
     * 這是 API 參數
     */
    const val API_EMAIL = "S_email"
    const val API_PASSWORD = "S_password"

    const val API_SID = "S_id"
    const val API_SNAME = "S_name"

    const val API_TID = "T_id"
    const val API_TNAME = "T_name"
    const val API_CNAME = "C_name"
    const val API_SIGN_ID = "Sign_id"
    const val API_RACE_ID = "Race_id"

    const val API_TEAMDESC_ID = "TeamDesc_id"
    const val API_TEAMLEADER_ID = "TeamLeader_id"
    const val API_TEAMMEMBER_ID = "TeamMember_id"

    /**
     * 這是 Beacon UUID
     */
    const val BEACON_UUID_SIGN = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa6"
    const val BEACON_UUID_RACE = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa5"
    const val BEACON_UUID_MAIN = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa4"
    const val BEACON_UUID_ANSWER = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa3"
    const val BEACON_UUID_LEADER = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa2"
    const val BEACON_UUID_MEMBER = "2f234454-cf6d-4a0f-adf2-f4911ba9ffa1"

}
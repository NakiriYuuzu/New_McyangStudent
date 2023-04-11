package tw.edu.studentmcyang.yuuzu_lib

import android.app.Activity
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * 以下是使用SharedPreference把資料存入手機內存
 * @param activity
 */
class SharedData(activity: Activity) {

    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

    /**
     * 登出時清除資料
     */
    fun logout() {
        saveSid("")
        saveSname("")
        quitCourse()
    }

    /**
     * 離開課程時清除資料
     */
    fun quitCourse() {
        saveSignID("")
        saveCourseId("")
        saveTname("")
        saveCourseName("")
        saveSignDate("")
    }

    /**
     * 設定是否為第一次開啟APP
     * @param status
     */
    fun saveSplashStatus(status: Boolean) {
        val editor = pref.edit()
        editor.putBoolean("splash_status", status)
        editor.apply()
    }

    /**
     * 取得是否為第一次開啟APP
     */
    fun getSplashStatus(): Boolean {
        return pref.getBoolean("splash_status", false)
    }

    /**
     * 存入帳號(這不建議把密碼存入手機內存)
     * @param account
     */
    fun saveLoginAcc(account: String) {
        val editor = pref.edit()
        editor.putString("login_acc", account)
        editor.apply()
    }

    /**
     * 取得帳號(這不建議把密碼存入手機內存)
     */
    fun getLoginAcc(): String {
        return pref.getString("login_acc", null).toString()
    }

    /**
     * 存入密碼(這不建議把密碼存入手機內存)
     * @param password
     */
    fun saveLoginPwd(password: String) {
        val editor = pref.edit()
        editor.putString("login_pwd", password)
        editor.apply()
    }

    /**
     * 取得密碼(這不建議把密碼存入手機內存)
     */
    fun getLoginPwd(): String {
        return pref.getString("login_pwd", null).toString()
    }

    /**
     * 存入學生ID
     * @param id
     */
    fun saveSid(id: String) {
        val editor = pref.edit()
        editor.putString("id", id)
        editor.apply()
    }

    /**
     * 取得學生ID
     */
    fun getSid(): String {
        return pref.getString("id", null).toString()
    }

    /**
     * 存入學生姓名
     * @param name
     */
    fun saveSname(name: String) {
        val editor = pref.edit()
        editor.putString("name", name)
        editor.apply()
    }

    /**
     * 取得學生姓名
     */
    fun getSname(): String {
        return pref.getString("name", null).toString()
    }

    /**
     * 存入學生簽到ID
     * @param signId
     */
    fun saveSignID(signId: String) {
        val editor = pref.edit()
        editor.putString("signId", signId)
        editor.apply()
    }

    /**
     * 取得學生簽到ID
     */
    fun getSignID(): String {
        return pref.getString("signId", null).toString()
    }

    /**
     * 存入學生簽到日期
     * @param signDate
     */
    fun saveSignDate(signDate: String) {
        val editor = pref.edit()
        editor.putString("signDate", signDate)
        editor.apply()
    }

    /**
     * 取得學生簽到日期
     */
    fun getSignDate(): String {
        return pref.getString("signDate", null).toString()
    }

    /**
     * 存入老師名稱
     * @param signStatus
     */
    fun saveTname(name: String) {
        val editor = pref.edit()
        editor.putString("tname", name)
        editor.apply()
    }

    /**
     * 取得老師名稱
     */
    fun getTname(): String {
        return pref.getString("tname", null).toString()
    }

    /**
     * 存入課程名稱
     * @param name
     */
    fun saveCourseName(name: String) {
        val editor = pref.edit()
        editor.putString("courseName", name)
        editor.apply()
    }

    /**
     * 取得課程名稱
     */
    fun getCourseName(): String {
        return pref.getString("courseName", null).toString()
    }

    /**
     * 存入課程ID
     * @param id
     */
    fun saveCourseId(id: String) {
        val editor = pref.edit()
        editor.putString("courseId", id)
        editor.apply()
    }

    /**
     * 取得課程ID
     */
    fun getCourseId(): String {
        return pref.getString("courseId", null).toString()
    }

    /**
     * 存入群組ID
     * @param id
     */
    fun saveTeamDescId(id: String) {
        val editor = pref.edit()
        editor.putString("teamDescId", id)
        editor.apply()
    }

    /**
     * 取得群組ID
     */
    fun getTeamDescId(): String {
        return pref.getString("teamDescId", null).toString()
    }
}
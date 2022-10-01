package tw.edu.studentmcyang.yuuzu_lib

import android.app.Activity
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedData(activity: Activity) {

    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

    fun logout() {
        saveSid("")
        saveSname("")
        quitCourse()
    }

    fun quitCourse() {
        saveSignID("")
        saveCourseId("")
        saveTname("")
        saveCourseName("")
        saveSignDate("")
    }

    fun saveSplashStatus(status: Boolean) {
        val editor = pref.edit()
        editor.putBoolean("splash_status", status)
        editor.apply()
    }

    fun getSplashStatus(): Boolean {
        return pref.getBoolean("splash_status", false)
    }

    fun saveLoginAcc(account: String) {
        val editor = pref.edit()
        editor.putString("login_acc", account)
        editor.apply()
    }

    fun getLoginAcc(): String {
        return pref.getString("login_acc", null).toString()
    }

    fun saveLoginPwd(password: String) {
        val editor = pref.edit()
        editor.putString("login_pwd", password)
        editor.apply()
    }

    fun getLoginPwd(): String {
        return pref.getString("login_pwd", null).toString()
    }

    fun saveSid(id: String) {
        val editor = pref.edit()
        editor.putString("id", id)
        editor.apply()
    }

    fun getSid(): String {
        return pref.getString("id", null).toString()
    }

    fun saveSname(name: String) {
        val editor = pref.edit()
        editor.putString("name", name)
        editor.apply()
    }

    fun getSname(): String {
        return pref.getString("name", null).toString()
    }

    fun saveSignID(signId: String) {
        val editor = pref.edit()
        editor.putString("signId", signId)
        editor.apply()
    }

    fun getSignID(): String {
        return pref.getString("signId", null).toString()
    }

    fun saveSignDate(signDate: String) {
        val editor = pref.edit()
        editor.putString("signDate", signDate)
        editor.apply()
    }

    fun getSignDate(): String {
        return pref.getString("signDate", null).toString()
    }

    fun saveTname(name: String) {
        val editor = pref.edit()
        editor.putString("tname", name)
        editor.apply()
    }

    fun getTname(): String {
        return pref.getString("tname", null).toString()
    }

    fun saveCourseName(name: String) {
        val editor = pref.edit()
        editor.putString("courseName", name)
        editor.apply()
    }

    fun getCourseName(): String {
        return pref.getString("courseName", null).toString()
    }

    fun saveCourseId(id: String) {
        val editor = pref.edit()
        editor.putString("courseId", id)
        editor.apply()
    }

    fun getCourseId(): String {
        return pref.getString("courseId", null).toString()
    }

    fun saveTeamDescId(id: String) {
        val editor = pref.edit()
        editor.putString("teamDescId", id)
        editor.apply()
    }

    fun getTeamDescId(): String {
        return pref.getString("teamDescId", null).toString()
    }
}
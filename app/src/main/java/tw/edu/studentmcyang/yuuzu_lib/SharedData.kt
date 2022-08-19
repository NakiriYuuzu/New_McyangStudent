package tw.edu.studentmcyang.yuuzu_lib

import android.app.Activity
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharedData(activity: Activity) {

    val pref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)

    fun logout() {
        saveID("")
        saveName("")
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

    fun saveID(id: String) {
        val editor = pref.edit()
        editor.putString("id", id)
        editor.apply()
    }

    fun getID(): String {
        return pref.getString("id", null).toString()
    }

    fun saveName(name: String) {
        val editor = pref.edit()
        editor.putString("name", name)
        editor.apply()
    }

    fun getName(): String {
        return pref.getString("name", null).toString()
    }

}
package tw.edu.studentmcyang.yuuzu_lib

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputEditText

/**
 * 隱藏鍵盤的方法
 * @param activity
 * ViewHelper(this).setupUI(findViewById(R.id.rootView))
 */
class ViewHelper(
    private val activity: Activity
) {
    /**
     * 設定點擊空白處隱藏鍵盤
     * @param view
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {
        if (view !is TextInputEditText) {
            view.setOnTouchListener { _: View?, _: MotionEvent? ->
                hideSoftKeyboard()
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    /**
     * 隱藏鍵盤
     */
    private fun hideSoftKeyboard() {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isAcceptingText) {
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}
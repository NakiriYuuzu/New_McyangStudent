package tw.edu.studentmcyang.yuuzu_lib

import android.app.Activity
import android.content.DialogInterface
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tw.edu.studentmcyang.R

class DialogHelper(
    private val activity: Activity
) {
    private var isLoading = false
    private val customView = activity.layoutInflater.inflate(R.layout.custom_dialog_loading, null, false)
    private val loadingAnimation = customView.findViewById<LottieAnimationView>(R.id.loading_animation)
    private val dialogBuilder = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
        .setView(customView)
        .setCancelable(false)
        .setTitle("loading")
        .create()

    fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_positive) { dialogInterface: DialogInterface?, i: Int ->
                dialogInterface?.dismiss()
            }
            .show()
    }

    fun showPositiveDialog(title: String, message: String, onPositiveListener: OnPositiveListener) {
        MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_positive) { dialogInterface: DialogInterface?, i: Int ->
                onPositiveListener.onPositiveClick(dialogInterface, i)
            }
            .show()
    }

    fun showFullDialog(title: String, message: String, onDialogListener: OnDialogListener) {
        MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_positive) { dialogInterface: DialogInterface?, i: Int ->
                onDialogListener.onPositiveClick(dialogInterface, i)
            }
            .setNegativeButton(R.string.alert_negative) { dialogInterface: DialogInterface?, i: Int ->
                onDialogListener.onNegativeClick(dialogInterface, i)
            }
            .show()
    }

    fun loadingDialog() {
        isLoading = true
        loadingAnimation.setAnimation(R.raw.loading)
        loadingAnimation.playAnimation()
        dialogBuilder.show()
    }

    fun dismissLoadingDialog() {
        isLoading = false
        loadingAnimation.cancelAnimation()
        dialogBuilder.dismiss()
    }

    fun dialogIsLoading(): Boolean {
        return isLoading
    }

    interface OnPositiveListener {
        fun onPositiveClick(dialogInterface: DialogInterface?, i: Int)
    }

    interface OnDialogListener {
        fun onPositiveClick(dialog: DialogInterface?, which: Int)
        fun onNegativeClick(dialog: DialogInterface?, which: Int)
    }
}
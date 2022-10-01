package tw.edu.studentmcyang.yuuzu_lib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import tw.edu.studentmcyang.R

class DialogHelper(
    private val activity: Activity
) {
    private var isLoading = false
    private var isScanning = false
    @SuppressLint("InflateParams")
    private val customView = activity.layoutInflater.inflate(R.layout.custom_dialog_loading, null, false)
    private val loadingAnimation = customView.findViewById<LottieAnimationView>(R.id.loading_animation)
    private var loadingDialog = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog).create()

    fun initLoadingDialog(title: String?) {
        loadingDialog = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setView(customView)
            .setCancelable(false)
            .setTitle(title)
            .create()
    }

    fun loadingDialog() {
        isLoading = true
        loadingAnimation.setAnimation(R.raw.loading)
        loadingAnimation.playAnimation()
        loadingDialog.show()
    }

    fun dismissLoadingDialog() {
        isLoading = false
        loadingAnimation.cancelAnimation()
        loadingDialog.dismiss()
    }

    fun dialogIsLoading(): Boolean {
        return isLoading
    }

    private lateinit var scanningAnimation: LottieAnimationView
    private var scanningDialog = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog).create()

    fun isScanningDialog(): Boolean {
        return isScanning
    }

    fun dismissScanningDialog() {
        if (isScanningDialog()) {
            isScanning = false
            scanningAnimation.cancelAnimation()
            scanningDialog.dismiss()
        }
    }

    fun scanningDialog() {
        loadingAnimation.setAnimation(R.raw.scanning)
        scanningAnimation.playAnimation()
        scanningDialog.show()
        isScanning = true
    }

    fun initScanningDialog(title: String, scanningPositiveListener: ScanningPositiveListener) {
        val scanningView = activity.layoutInflater.inflate(R.layout.custom_dialog_scanning, null, false)
        scanningAnimation = scanningView.findViewById(R.id.scanning_animation)
        scanningDialog = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setView(scanningView)
            .setCancelable(false)
            .setTitle(title)
            .setPositiveButton(R.string.alert_leave, null)
            .create()

        scanningDialog.setOnShowListener {
            val positiveButton = scanningDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.apply {
                visibility = View.GONE
                Handler(Looper.getMainLooper()).postDelayed({
                    visibility = View.VISIBLE
                }, 3000)

                setOnClickListener {
                    scanningPositiveListener.onPositiveClick(it, scanningDialog)
                }
            }
        }
    }

    fun textInputDialog(title: String, customTextPositiveListener: CustomTextPositiveListener) {
        val textInputView = activity.layoutInflater.inflate(R.layout.custom_dialog_text, null, false)
        val inputText = textInputView.findViewById<TextInputEditText>(R.id.race_Input)
        val textInputDialog = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setView(textInputView)
            .setCancelable(false)
            .setTitle(title)
            .setPositiveButton(R.string.alert_positive, null)
            .setNegativeButton(R.string.alert_negative) { dialogInterface: DialogInterface?, _: Int ->
                dialogInterface?.dismiss()
                activity.finish()
            }
            .create()

        textInputDialog.setOnShowListener {
            val positiveButton = textInputDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                customTextPositiveListener.onPositiveTextClick(it, textInputDialog, inputText)
            }
        }

        textInputDialog.show()
    }

    fun dropDownDialog(title: String, autoTextAdapter: ArrayAdapter<String>, customPositiveListener: CustomPositiveListener, onItemClickListener: OnItemClickListener) {
        val dropdownView = activity.layoutInflater.inflate(R.layout.custom_dialog_dropdown, null, false)
        val autoText = dropdownView.findViewById<AutoCompleteTextView>(R.id.customDropDown)
        val dropDownDialog = MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setView(dropdownView)
            .setCancelable(false)
            .setTitle(title)
            .setPositiveButton(R.string.alert_positive, null)
            .setNegativeButton(R.string.alert_negative) { dialogInterface: DialogInterface?, _: Int ->
                dialogInterface?.dismiss()
                activity.finish()
            }
            .create()

        autoText.setAdapter(autoTextAdapter)
        autoText.setOnItemClickListener{ adapterView, view, i, l ->
            onItemClickListener.onItemClick(adapterView, view, i, l)
        }

        dropDownDialog.setOnShowListener {
            val positiveButton = dropDownDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener{
                customPositiveListener.onPositiveClick(it, dropDownDialog)
            }
        }

        dropDownDialog.show()
    }

    fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.alert_positive) { dialogInterface: DialogInterface?, _: Int ->
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
            .setNegativeButton(R.string.alert_negative) { dialogInterface: DialogInterface?, _: Int ->
                dialogInterface?.dismiss()
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

    fun showCustomButtonDialog(title: String, message: String, positiveText: String, negativeText: String, onDialogListener: OnDialogListener) {
        MaterialAlertDialogBuilder(activity, R.style.Style_CustomDialog)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(positiveText) { dialogInterface: DialogInterface?, i: Int ->
                onDialogListener.onPositiveClick(dialogInterface, i)
            }
            .setNegativeButton(negativeText) { dialogInterface: DialogInterface?, i: Int ->
                onDialogListener.onNegativeClick(dialogInterface, i)
            }.show()
    }

    fun sweetDialog(title: String, message: String, status: Int, sweetPositiveDialogListener: SweetPositiveDialogListener?) {
        SweetAlertDialog(activity, status).apply {
            titleText = title
            contentText = message
            confirmText = activity.getString(R.string.alert_positive)
            setConfirmClickListener {
                sweetPositiveDialogListener?.onPositiveClick(it)
                it.dismiss()
            }
            setCancelable(false)
        }.show()
    }

    fun sweetBtnDialog(title: String, message: String, cancelable: Boolean, status: Int, sweetDialogListener: SweetDialogListener) {
        SweetAlertDialog(activity, status).apply {
            setCancelable(cancelable)
            titleText = title
            contentText = message
            confirmText = activity.getString(R.string.alert_positive)
            cancelText = activity.getString(R.string.alert_negative)
            setConfirmClickListener {
                sweetDialogListener.onPositiveClick(it)
            }
            setCancelClickListener {
                sweetDialogListener.onNegativeClick(it)
            }
        }.show()
    }

    interface OnPositiveListener {
        fun onPositiveClick(dialogInterface: DialogInterface?, i: Int)
    }

    interface OnDialogListener {
        fun onPositiveClick(dialog: DialogInterface?, which: Int)
        fun onNegativeClick(dialog: DialogInterface?, which: Int)
    }

    interface ScanningPositiveListener {
        fun onPositiveClick(view: View, dialog: androidx.appcompat.app.AlertDialog)
    }

    interface SweetDialogListener {
        fun onPositiveClick(dialog: SweetAlertDialog)
        fun onNegativeClick(dialog: SweetAlertDialog)
    }

    interface SweetPositiveDialogListener {
        fun onPositiveClick(dialog: SweetAlertDialog)
    }

    interface CustomTextPositiveListener {
        fun onPositiveTextClick(
            var1: View,
            dropDownDialog: androidx.appcompat.app.AlertDialog,
            inputText: TextInputEditText
        )
    }

    interface CustomPositiveListener {
        fun onPositiveClick(var1: View, dialog: androidx.appcompat.app.AlertDialog)
    }

    interface OnItemClickListener {
        fun onItemClick(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long)
    }
}
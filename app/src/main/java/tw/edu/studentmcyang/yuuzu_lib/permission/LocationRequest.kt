package tw.edu.studentmcyang.yuuzu_lib.permission

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.provider.Settings
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper


class LocationRequest(private val activity: Activity) : MultiplePermissionsListener {
    fun requestLocation() {
        Dexter.withContext(activity)
            .withPermissions(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ).withListener(this)
            .check()
    }

    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
        p0.let {
            if (p0?.areAllPermissionsGranted() == false) {
                val dialogHelper = DialogHelper(activity)
                dialogHelper.showPositiveDialog("GPS", "Please turn on GPS", object :
                    DialogHelper.OnPositiveListener {
                    override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                        activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        dialogInterface?.dismiss()
                    }
                })
            }
        }
    }

    override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?) {
        p1?.continuePermissionRequest()
    }
}
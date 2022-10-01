package tw.edu.studentmcyang.activity.main.race

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.android.material.snackbar.Snackbar
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.json.JSONException
import org.json.JSONObject
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.databinding.ActivityRaceBinding
import tw.edu.studentmcyang.yuuzu_lib.*
import tw.edu.studentmcyang.yuuzu_lib.model.BeaconDto
import tw.edu.studentmcyang.yuuzu_lib.model.MessageListener
import kotlin.concurrent.thread

class RaceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRaceBinding

    private var count = 0
    private var wbCount = 0
    private var isStart = false
    private lateinit var beaconList: ArrayList<BeaconDto>

    private lateinit var yuuzuApi: YuuzuApi
    private lateinit var sharedData: SharedData
    private lateinit var dialogHelper: DialogHelper
    private lateinit var beaconController: BeaconController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initButton()
        initDialog()
        initBeacon()
    }

    private fun getResult() {
        yuuzuApi.api(
            Request.Method.GET,
            AppConfig.URL_LIST_RACE + "?${AppConfig.API_RACE_ID}=${beaconList[0].minor}&${AppConfig.API_SID}=${sharedData.getSid()}",
            object :
                YuuzuApi.YuuzuApiListener {
                override fun onSuccess(data: String) {
                    try {
                        val jsonObject = JSONObject(data)
                        val answer = jsonObject.getString("Answer")

                        if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()

                        if (answer == "1") {
                            dialogHelper.sweetDialog(getString(R.string.race_alert_EndMessage_Success), "", SweetAlertDialog.SUCCESS_TYPE, object :
                                DialogHelper.SweetPositiveDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()
                                    onLeave()
                                    finish()
                                }
                            })
                        } else {
                            dialogHelper.sweetDialog(getString(R.string.race_alert_EndMessage_Failed), "", SweetAlertDialog.ERROR_TYPE, object :
                                DialogHelper.SweetPositiveDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()
                                    onLeave()
                                    finish()
                                }
                            })
                        }

                    } catch (e: JSONException) {
                        dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                            DialogHelper.SweetPositiveDialogListener {
                            override fun onPositiveClick(dialog: SweetAlertDialog) {
                                dialog.dismiss()
                                onLeave()
                                finish()
                            }
                        })
                    }
                }

                override fun onError(error: VolleyError) {
                    if (error.networkResponse != null) {
                        when (error.networkResponse.statusCode) {
                            400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null) }
                            500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "",SweetAlertDialog.ERROR_TYPE, null) }
                        }

                    } else {
                        dialogHelper.sweetDialog(getString(R.string.alert_error_title_noInternet), getString(R.string.alert_error_noInternet), SweetAlertDialog.ERROR_TYPE, object :
                            DialogHelper.SweetPositiveDialogListener {
                            override fun onPositiveClick(dialog: SweetAlertDialog) {
                                dialog.dismiss()
                                finish()
                            }
                        })
                    }
                }

                override val params: Map<String, String>
                    get() = mapOf()
            })
    }

    private fun initWebSocket() {
        WebSocketManager.init(AppConfig.WS_RACE, object : MessageListener {
            override fun onConnectSuccess() {
                Log.e(AppConfig.TAG, "onConnectSuccess: ")
            }

            override fun onConnectFailed() {
                Log.e(AppConfig.TAG, "onConnectFailed: ")
                if (isStart) WebSocketManager.reconnect()
                if (wbCount == 3) {
                    Snackbar.make(binding.root, getString(R.string.alert_error_title_noInternet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.alert_positive)){
                            isStart = false
                            onLeave()
                            finish()
                    }.show()
                }
                wbCount++
            }

            override fun onClose() {
                Log.e(AppConfig.TAG, "onClose: ")
            }

            override fun onMessage(text: String?) {
                try {
                    if (!text.isNullOrBlank()) {
                        val jsonObject = JSONObject(text)
                        val rId = jsonObject.getString(AppConfig.API_RACE_ID)

                        if (rId == beaconList[0].minor) {
                            isStart = false
                            getResult()
                        }
                    }
                } catch (e: JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            onLeave()
                            finish()
                        }
                    })
                }
            }
        })

        thread {
            kotlin.run {
                if (!isStart) return@thread
                WebSocketManager.connect()
            }
        }
    }

    private fun sendData() {
        yuuzuApi.api(Request.Method.POST, AppConfig.URL_PUSH_RACE, object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                dialogHelper.initLoadingDialog(getString(R.string.race_btn_SuccessMessage))

                dialogHelper.sweetDialog(getString(R.string.race_btn_SuccessTitle), getString(R.string.race_btn_SuccessMessage), SweetAlertDialog.SUCCESS_TYPE, object :
                    DialogHelper.SweetPositiveDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        if (isStart) dialogHelper.loadingDialog()
                        dialog.dismiss()
                    }
                })

                isStart = true
                initWebSocket()
            }

            override fun onError(error: VolleyError) {
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null) }
                        406 -> {dialogHelper.sweetDialog("老師已關閉搶答！", "",SweetAlertDialog.ERROR_TYPE, null) }
                        417 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_417), "",SweetAlertDialog.ERROR_TYPE, null) }
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "",SweetAlertDialog.ERROR_TYPE, null) }
                    }

                } else {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_noInternet), getString(R.string.alert_error_noInternet), SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            finish()
                        }
                    })
                }
            }

            override val params: Map<String, String>
                get() = mapOf(
                    AppConfig.API_SID to sharedData.getSid(),
                    AppConfig.API_SNAME to sharedData.getSname(),
                    AppConfig.API_RACE_ID to beaconList[0].minor
                )
        })
    }

    private fun getDoc() {
        yuuzuApi.api(Request.Method.GET, AppConfig.URL_LIST_RACEANSWER + "?${AppConfig.API_RACE_ID}=${beaconList[0].minor}", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    val jsonObject = JSONObject(data)
                    binding.raceQuestion.text = jsonObject.getString("Race_doc")
                    if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()
                    if (beaconController.isScanning()) beaconController.stopScanning()
                    binding.raceBtnRace.playAnimation()

                } catch (e : JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            onLeave()
                            finish()
                        }
                    })
                }
            }

            override fun onError(error: VolleyError) {
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null) }
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "",SweetAlertDialog.ERROR_TYPE, null) }
                    }

                } else {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_noInternet), getString(R.string.alert_error_noInternet), SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            finish()
                        }
                    })
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    private fun initBeacon() {
        beaconController.startScanning(object : BeaconController.BeaconModify {
            override fun modifyData(beacons: Collection<Beacon?>?, region: Region?) {
                if (beacons?.isNotEmpty() == true) {
                    count = 0

                    beacons.forEach {
                        if (!beaconList.any { dto -> dto.minor == it?.id3.toString() }) {
                            beaconList.add(
                                BeaconDto(
                                    uuid = it?.id1.toString(),
                                    major = it?.id2.toString(),
                                    minor = it?.id3.toString()
                                )
                            )

                            getDoc()
                            Log.e(AppConfig.TAG, "modifyData: $beaconList")
                        }
                    }
                } else {
                    if (count == 30) {
                        dialogHelper.sweetDialog(getString(R.string.alert_error_bleLollipop), "", SweetAlertDialog.ERROR_TYPE, object :
                            DialogHelper.SweetPositiveDialogListener {
                            override fun onPositiveClick(dialog: SweetAlertDialog) {
                                dialog.dismiss()
                                onLeave()
                                finish()
                            }
                        })
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        count ++
                    }, 1000)
                }
            }
        })
    }

    private fun initDialog() {
        dialogHelper.initScanningDialog("等待老師廣播...", object : DialogHelper.ScanningPositiveListener {
            override fun onPositiveClick(view: View, dialog: AlertDialog) {
                dialogHelper.sweetBtnDialog(
                    getString(R.string.alert_leave_Title),
                    getString(R.string.alert_leave_Message),
                    false,
                    SweetAlertDialog.WARNING_TYPE, object :
                    DialogHelper.SweetDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        onLeave()
                        finish()
                        dialog.dismiss()
                    }

                    override fun onNegativeClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                    }
                })
            }
        })

        dialogHelper.scanningDialog()
    }

    private fun initButton() {
        binding.raceBtnBack.setOnClickListener {
            dialogHelper.sweetBtnDialog(
                title = getString(R.string.alert_leave_Title),
                message = getString(R.string.alert_leave_Message),
                cancelable = false,
                status = SweetAlertDialog.WARNING_TYPE,
                object : DialogHelper.SweetDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        onLeave()
                        finish()
                        dialog.dismiss()
                    }

                    override fun onNegativeClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                    }
                }
            )
        }

        binding.raceBtnRace.setOnClickListener {
            sendData()
            stopAnimation()
        }
    }

    private fun stopAnimation() {
        if (binding.raceBtnRace.isAnimating) {
            binding.raceBtnRace.progress = 0F
            binding.raceBtnRace.cancelAnimation()
        }

        binding.raceBtnRace.isEnabled = false
    }

    private fun initView() {
        yuuzuApi = YuuzuApi(this)
        sharedData = SharedData(this)
        dialogHelper = DialogHelper(this)
        beaconController = BeaconController(this, Region("Race", Identifier.parse(AppConfig.BEACON_UUID_RACE), null, null))

        beaconList = ArrayList()
    }

    private fun onLeave() {
        if (WebSocketManager.isConnect()) WebSocketManager.close()
        if (beaconController.isScanning()) beaconController.stopScanning()
        if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()
        if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        onLeave()
    }
}
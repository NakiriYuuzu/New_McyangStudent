package tw.edu.studentmcyang.activity.main.sign

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.VolleyError
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.sign.adapter.SignAdapter
import tw.edu.studentmcyang.activity.main.sign.model.Sign
import tw.edu.studentmcyang.databinding.ActivitySignBinding
import tw.edu.studentmcyang.yuuzu_lib.*
import tw.edu.studentmcyang.yuuzu_lib.model.BeaconDto

class SignActivity : AppCompatActivity() {

    private var countTimer = 0
    private lateinit var beaconDtoList: ArrayList<BeaconDto>
    private lateinit var signList: ArrayList<Sign>

    private lateinit var binding: ActivitySignBinding

    private lateinit var signAdapter: SignAdapter

    private lateinit var yuuzuApi: YuuzuApi
    private lateinit var sharedData: SharedData
    private lateinit var dialogHelper: DialogHelper
    private lateinit var beaconController: BeaconController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initButton()
        initDialog()
        initBeacon()
        initRecyclerView()
    }

    /**
     * 送出簽到資料
     */
    private fun signSubmit(sign: Sign) {
        yuuzuApi.api(Request.Method.POST, AppConfig.URL_PUSH_SIGN_COURSE, object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    val jsonObject = JSONObject(data)
                    val signDate = jsonObject.getString("Crt_time")

                    // save to shared data
                    sharedData.saveSignID(sign.Sign_id)
                    sharedData.saveCourseId(jsonObject.getString("C_id"))
                    sharedData.saveCourseName(sign.C_Name)
                    sharedData.saveSignDate(signDate)
                    sharedData.saveTname(sign.T_Name)

                    Log.e(AppConfig.TAG, "onSuccess: $signDate")

                    dialogHelper.sweetDialog(getString(R.string.sign_text_signup_message), "", SweetAlertDialog.SUCCESS_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            finish()
                            dialog.dismiss()
                        }
                    })

                } catch (e: JSONException) {
                    dialogHelper.showDialog(getString(R.string.alert_error_title_json), getString(R.string.alert_error_json) + e.message)
                }
            }

            override fun onError(error: VolleyError) {
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog("您已經簽到過！", "", SweetAlertDialog.ERROR_TYPE, null) }
                        404 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_404), "", SweetAlertDialog.ERROR_TYPE, null) }
                        406 -> {dialogHelper.sweetDialog("您不是此課堂的學生！", "",SweetAlertDialog.ERROR_TYPE, null) }
                        417 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_417), "", SweetAlertDialog.ERROR_TYPE, null) }
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
                    AppConfig.API_SIGN_ID to sign.Sign_id
                )
        })
    }

    /**
     * 初始化 RecyclerView
     */
    private fun initRecyclerView() {
        signAdapter = SignAdapter(object : SignAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                dialogHelper.showFullDialog(
                    getString(R.string.sign_alert_title_Send),
                    getString(R.string.sign_alert_title_Send_Detail) + signList[position].C_Name,
                    object : DialogHelper.OnDialogListener {
                        override fun onPositiveClick(dialog: DialogInterface?, which: Int) {
                            signSubmit(signList[position])
                            dialog?.dismiss()
                        }

                        override fun onNegativeClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                        }
                    }
                )
            }
        })

        signAdapter.submitList(signList)
        binding.signRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@SignActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = this@SignActivity.signAdapter
        }
    }

    /**
     * 資料同步
     */
    private fun syncView(result: ArrayList<Sign>) {
        val list = ArrayList(signList)
        result.forEach {
            if (!list.any { l -> l.Sign_id == it.Sign_id }) {
                list.add(
                    it
                )
            }
        }

        signList = list
        signAdapter.submitList(list)
    }

    /**
     * 請求資料每當接受到beacon的時候(一次)
     */
    private fun requestData() {
        beaconDtoList.forEach {
            yuuzuApi.api(Request.Method.GET, AppConfig.URL_LIST_SIGN_COURSE + "?id=${it.minor}", object: YuuzuApi.YuuzuApiListener{
                override fun onSuccess(data: String) {
                    val jsonArray = JSONArray(data)
                    val list = ArrayList<Sign>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        list.add(
                            Sign(
                                C_Name = jsonObject.getString(AppConfig.API_CNAME),
                                Sign_id = jsonObject.getString(AppConfig.API_SIGN_ID),
                                T_Name = jsonObject.getString(AppConfig.API_TNAME) + "老師"
                            )
                        )
                    }

                    syncView(list)
                }

                override fun onError(error: VolleyError) {
                    dialogHelper.showDialog(getString(R.string.alert_error_title_noInternet), getString(R.string.alert_error_noInternet))
                }

                override val params: Map<String, String>
                    get() = mapOf()
            })
        }
    }

    /**
     * 初始化beacon，并且監聽簽到beacon
     */
    private fun initBeacon() {
        beaconController.startScanning(object : BeaconController.BeaconModify {
            override fun modifyData(beacons: Collection<Beacon?>?, region: Region?) {
                if (beacons?.isNotEmpty() == true) {
                    countTimer = 0

                    if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()

                    beacons.forEach {
                        if (!beaconDtoList.any { dto -> dto.minor == it?.id3.toString() }) {
                            beaconDtoList.add(
                                BeaconDto(
                                uuid = it?.id1.toString(),
                                major = it?.id2.toString(),
                                minor = it?.id3.toString()
                                )
                            )

                            requestData()
                        }
                    }

                } else {
                    if (beaconDtoList.size != 0) return

                    if (countTimer == 30) {
                        dialogHelper.sweetBtnDialog(
                            title = getString(R.string.alert_error_bleLollipop),
                            message = "",
                            cancelable = false,
                            status = SweetAlertDialog.WARNING_TYPE,
                            object : DialogHelper.SweetDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    if (beaconController.isScanning()) beaconController.stopScanning()
                                    if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()

                                    beaconController.fixLollipop()
                                    finish()
                                    dialog.dismiss()
                                }

                                override fun onNegativeClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()
                                }
                            }
                        )
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        countTimer++
                    }, 1000)
                }
            }
        })
    }

    private fun initDialog() {
        dialogHelper.initScanningDialog("等待老師廣播...", object : DialogHelper.ScanningPositiveListener {
            override fun onPositiveClick(view: View, dialog: AlertDialog) {
                dialogHelper.sweetBtnDialog(
                    title = getString(R.string.alert_leave_Title),
                    message = getString(R.string.alert_leave_Message),
                    cancelable = false,
                    status = SweetAlertDialog.WARNING_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            if (beaconController.isScanning()) beaconController.stopScanning()
                            if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()

                            finish()
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                        }
                    }
                )
            }
        })

        dialogHelper.scanningDialog()
    }

    private fun initButton() {
        binding.signBtnBack.setOnClickListener {
            dialogHelper.showPositiveDialog(getString(R.string.sign_error_title_Leave), getString(R.string.sign_error_title_Leave_detail), object:
                DialogHelper.OnPositiveListener {
                override fun onPositiveClick(dialogInterface: DialogInterface?, i: Int) {
                    finish()
                    dialogInterface?.dismiss()
                }
            })
        }
    }

    private fun initView() {
        beaconDtoList = ArrayList()
        signList = ArrayList()

        yuuzuApi = YuuzuApi(this)
        sharedData = SharedData(this)
        dialogHelper = DialogHelper(this)
        beaconController = BeaconController(this, region = Region("", Identifier.parse(AppConfig.BEACON_UUID_SIGN), null, null))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (beaconController.isScanning()) beaconController.stopScanning()
        if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()
    }
}
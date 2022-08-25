package tw.edu.studentmcyang.activity.main.sign

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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
import tw.edu.studentmcyang.model.BeaconDto
import tw.edu.studentmcyang.yuuzu_lib.BeaconController
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData
import tw.edu.studentmcyang.yuuzu_lib.YuuzuApi

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
        initBeacon()
        initRecyclerView()
    }

    private fun signSubmit() {
        yuuzuApi.api(Request.Method.POST, AppConfig.URL_PUSH_SIGN_COURSE, object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    val jsonObject = JSONObject(data)
                    val status = jsonObject.getString("status")

                    if (status == "true") {
                        Toast.makeText(
                            this@SignActivity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return
                    }

                    if (status == "false") {
                        Toast.makeText(
                            this@SignActivity,
                            jsonObject.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                } catch (e: JSONException) {
                    dialogHelper.showDialog(getString(R.string.alert_error_title_json), getString(R.string.alert_error_json) + e.message)
                }
            }

            override fun onError(error: VolleyError) {
                dialogHelper.showDialog(getString(R.string.alert_error_title_noInternet), getString(R.string.alert_error_noInternet))
            }

            override val params: Map<String, String>
                get() = mapOf(
                    "S_id" to sharedData.getID(),
                    "Sign_id" to sharedData.getSignID()
                )
        })
    }

    private fun initRecyclerView() {
        signAdapter = SignAdapter(object : SignAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                dialogHelper.showFullDialog(
                    getString(R.string.sign_alert_title_Send),
                    getString(R.string.sign_alert_title_Send_Detail) + signList[position].C_Name,
                    object : DialogHelper.OnDialogListener {
                        override fun onPositiveClick(dialog: DialogInterface?, which: Int) {
                            sharedData.saveSignID(signList[position].Sign_id)
                            signSubmit()
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
                                C_Name = jsonObject.getString("C_Name"),
                                Sign_id = jsonObject.getString("Sign_id"),
                                T_Name = jsonObject.getString("T_Name") + "老師"
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

    private fun initBeacon() {
        beaconController.startScanning(object : BeaconController.BeaconModify {
            override fun modifyData(beacons: Collection<Beacon?>?, region: Region?) {
                if (beacons?.isNotEmpty() == true) {
                    countTimer = 0

                    if (dialogHelper.dialogIsLoading()) {
                        dialogHelper.dismissLoadingDialog()
                    }

                    beacons.forEach {
                        if (!beaconDtoList.any { dto -> dto.minor == it?.id3.toString() }) {
                            beaconDtoList.add(BeaconDto(
                                uuid = it?.id1.toString(),
                                major = it?.id2.toString(),
                                minor = it?.id3.toString()
                            ))

                            requestData()
                        }
                    }

                } else {
                    if (beaconDtoList.size != 0) {
                        return
                    }

                    if (countTimer == AppConfig.TIMEOUT) {
                        if (dialogHelper.dialogIsLoading()) {
                            dialogHelper.showDialog(getString(R.string.sign_error_title_Timeout), getString(R.string.sign_error_title_Timeout_detail))
                            dialogHelper.dismissLoadingDialog()
                        }

                        if (beaconController.isScanning()) {
                            beaconController.stopScanning()
                            beaconController.fixLollipop()
                        }
                        return
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        countTimer++
                    }, 1000)
                }
            }
        })
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

        dialogHelper.loadingDialog()
    }

    override fun onResume() {
        super.onResume()
        if (!beaconController.isScanning()) {
            initBeacon()
        }
    }

    override fun onPause() {
        super.onPause()
        if (beaconController.isScanning()) {
            beaconController.stopScanning()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (beaconController.isScanning()) {
            beaconController.stopScanning()
        }
    }
}
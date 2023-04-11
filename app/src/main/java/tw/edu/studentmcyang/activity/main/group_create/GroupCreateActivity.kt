package tw.edu.studentmcyang.activity.main.group_create

import android.content.Intent
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
import com.google.android.material.snackbar.Snackbar
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.databinding.ActivityGroupCreateBinding
import tw.edu.studentmcyang.yuuzu_lib.*
import tw.edu.studentmcyang.yuuzu_lib.anim.fadeIn
import tw.edu.studentmcyang.yuuzu_lib.anim.fadeOut
import tw.edu.studentmcyang.yuuzu_lib.model.MessageListener
import kotlin.concurrent.thread

class GroupCreateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupCreateBinding

    private lateinit var yuuzuApi: YuuzuApi
    private lateinit var sharedData: SharedData
    private lateinit var dialogHelper: DialogHelper
    private lateinit var beaconController: BeaconController

    private lateinit var teamAdapter: TeamAdapter
    private lateinit var leaderAdapter: LeaderAdapter

    private var teamList = ArrayList<TeamDto>()

    private var countTimer = 0
    private var wbCount = 0

    private var isLeadering = false
    private var isMembering = false
    private var afterPick = false
    private var syncData = false

    private var teamLeaderId = ""
    private var teamDescId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initButton()
        initDialog()
    }

    /**
     * 初始化畫面, 顯示第2個畫面
     */
    private fun initScene2View(status: Int) {
        when (status) {
            1 -> {
                binding.groupCreateLinearLayoutScene1.fadeOut(1000L, 0L)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.groupCreateLinearLayoutScene1.visibility = View.GONE
                    binding.groupCreateCardViewScene2.visibility = View.VISIBLE
                    binding.groupCreateCardViewScene2.fadeIn(1000L, 0L)
                }, 900L)
            }

            2 -> {
                binding.groupCreateCardViewSceneMember.fadeOut(1000L, 0L)
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.groupCreateCardViewSceneMember.visibility = View.GONE
                    binding.groupCreateCardViewScene2.visibility = View.VISIBLE
                    // binding.groupCreateCardViewScene2.fadeIn(1000L, 0L)
                }, 900L)
            }
        }

        binding.groupCreateTitleScene2.text = getString(R.string.groupCreate_WaitingMember)

        teamAdapter = TeamAdapter()
        binding.groupCreateRecyclerViewScene2.apply {
            val linearLayoutManager = LinearLayoutManager(this@GroupCreateActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = this@GroupCreateActivity.teamAdapter
        }

        teamAdapter.differ.submitList(teamList)
    }

    /**
     * 同步資料
     * @param leader_id
     */

    private fun syncData(leader_id: String) {
        if (syncData) return
        syncData = true

        yuuzuApi.api(
            Request.Method.GET, AppConfig.URL_LIST_TEAMMEMBER + "?${AppConfig.API_TEAMDESC_ID}=$teamDescId&${AppConfig.API_TEAMLEADER_ID}=$leader_id", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    teamList = ArrayList()
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val teamDto = TeamDto(
                            id = jsonObject.getString("Id"),
                            teamName = jsonObject.getString(AppConfig.API_SNAME),
                            isLeader = jsonObject.getBoolean("IsLeader"),
                        )

                        teamList.add(teamDto)
                    }

                    syncData = false
                    teamAdapter.differ.submitList(teamList)

                } catch (e : JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            theEND()
                            finish()
                        }
                    })
                }
            }

            override fun onError(error: VolleyError) {
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null) }
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "", SweetAlertDialog.ERROR_TYPE, null) }
                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    /**
     * 同步組長
     */
    private fun syncLeader() {
        if (afterPick) return
        afterPick = true
        yuuzuApi.api(Request.Method.GET, AppConfig.URL_LIST_TEAMLEADER + "?${AppConfig.API_TEAMLEADER_ID}=$teamLeaderId", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                Log.e(AppConfig.TAG, "onSuccess: $data")
                if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()

                // setup UI
                initScene2View(1)
                dialogHelper.sweetDialog("您以被老師選爲組長", getString(R.string.groupCreate_WaitingMember), SweetAlertDialog.SUCCESS_TYPE, null)

                teamList = ArrayList()
                teamList.add(TeamDto(id = teamLeaderId, teamName = sharedData.getSname(), isLeader = true))
                teamAdapter.differ.submitList(teamList)
            }

            override fun onError(error: VolleyError) {
                if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        406 -> {dialogHelper.sweetDialog("抱歉，您沒入選組長資格", "請麻煩${sharedData.getSname()}同學選擇組員!",SweetAlertDialog.ERROR_TYPE, object :
                            DialogHelper.SweetPositiveDialogListener {
                            override fun onPositiveClick(dialog: SweetAlertDialog) {
                                theEND()
                                finish()
                                startActivity(Intent(this@GroupCreateActivity, GroupCreateActivity::class.java))
                            }
                        }) }
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "",SweetAlertDialog.ERROR_TYPE, null) }
                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    /**
     * 設定webSocket
     */
    private fun initWebSocket() {
        WebSocketManager.init(AppConfig.WS_GROUPLIST, object : MessageListener {
            override fun onConnectSuccess() {
                Log.e(AppConfig.TAG, "onConnectSuccess")
            }

            override fun onConnectFailed() {
                Log.e(AppConfig.TAG, "onConnectFailed: ")
                if (isLeadering || isMembering) WebSocketManager.reconnect()
                if (wbCount == 3) {
                    Snackbar.make(binding.root, getString(R.string.alert_error_title_noInternet), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.alert_positive)){
                            theEND()
                            finish()
                        }.show()
                }
                wbCount++
            }

            override fun onClose() {
                Log.e(AppConfig.TAG, "onClose")
                isLeadering = false
                isMembering = false
            }

            override fun onMessage(text: String?) {
                try {
                    Log.e(AppConfig.TAG, "onMessage: $text")
                    if (text.isNullOrBlank()) return
                    val jsonObject = JSONObject(text)
                    val teamDescId = jsonObject.getString(AppConfig.API_TEAMDESC_ID)
                    // val identity = jsonObject.getString("Identity")
                    val leader = jsonObject.getString("Leader")
                    val member = jsonObject.getString("Member")

                    if (teamDescId != this@GroupCreateActivity.teamDescId) return

                    if (leader == "1") syncLeader()

                    if (member == "1") syncData(teamLeaderId)

                    // if (identity == "0") return

                    if (leader == "0" && member == "0") {
                        isLeadering = false
                        isMembering = false

                        Handler(Looper.getMainLooper()).postDelayed({
                            dialogHelper.sweetDialog("組隊完成！", "正在返回主畫面", SweetAlertDialog.SUCCESS_TYPE, object :
                                DialogHelper.SweetPositiveDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()
                                    theEND()
                                    finish()
                                }
                            })
                        }, 500)
                    }

                } catch (e: JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            theEND()
                            finish()
                        }
                    })
                }
            }
        })

        thread {
            kotlin.run {
                if (isLeadering) WebSocketManager.connect()
                if (isMembering) WebSocketManager.connect()
            }
        }
    }

    /**
     * 送出組長資料(API)
     */
    private fun sendData(url: String, map: Map<String, String>) {
        yuuzuApi.api(Request.Method.POST, url, object : YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()

                binding.groupCreateBtnLeader.isEnabled = false
                binding.groupCreateBtnMember.isEnabled = false

                try {
                    when (url) {
                        AppConfig.URL_CREATE_TEAMLEADER -> {
                            isLeadering = true

                            val jsonObject = JSONObject(data)
                            teamLeaderId = jsonObject.getString(AppConfig.API_TEAMLEADER_ID)

                            initWebSocket()

                            dialogHelper.sweetDialog("送出成功！", getString(R.string.groupCreate_WaitingTeacher), SweetAlertDialog.SUCCESS_TYPE, object :
                                DialogHelper.SweetPositiveDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()
                                    // Setup Ui
                                    if (!dialogHelper.isScanningDialog()) dialogHelper.scanningDialog()
                                }
                            })
                        }
                        AppConfig.URL_CREATE_TEAMMEMBER -> {
                            isMembering = true
                            initWebSocket()
                            dialogHelper.sweetDialog("送出成功！", "成功加入", SweetAlertDialog.SUCCESS_TYPE, object :
                                DialogHelper.SweetPositiveDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()
                                    // reset Data
                                    teamList = ArrayList()
                                    teamList.add(TeamDto(
                                        id = map[AppConfig.API_TEAMLEADER_ID].toString(),
                                        teamName = map[AppConfig.API_SNAME].toString(),
                                        isLeader = true,
                                    ))
                                    teamList.add(TeamDto(
                                        id = sharedData.getSid(),
                                        teamName = sharedData.getSname(),
                                        isLeader = false,
                                    ))

                                    Log.e(AppConfig.TAG, "onPositiveClick: $teamList")

                                    // Setup Ui
                                    initScene2View(2)
                                }
                            })
                        }
                    }


                } catch (e : JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            theEND()
                            finish()
                        }
                    })
                }
            }

            override fun onError(error: VolleyError) {
                if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()

                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null) }
                        404 -> {dialogHelper.sweetDialog("人數已上限！", "", SweetAlertDialog.ERROR_TYPE, null) }
                        406 -> {dialogHelper.sweetDialog("不能同時申請兩個隊伍！", "",SweetAlertDialog.ERROR_TYPE, null) }
                        410 -> {dialogHelper.sweetDialog("隊長不能兼任隊員！", "", SweetAlertDialog.ERROR_TYPE, null) }
                        417 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_417), "", SweetAlertDialog.ERROR_TYPE, null) }
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "",SweetAlertDialog.ERROR_TYPE, null) }
                    }
                }
            }

            override val params: Map<String, String>
                get() = map
        })
    }

    /**
     * 選擇隊長
     */
    private fun initMemberView() {
        binding.groupCreateLinearLayoutScene1.fadeOut(1000L, 0L)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.groupCreateLinearLayoutScene1.visibility = View.GONE
            binding.groupCreateCardViewSceneMember.visibility = View.VISIBLE
            binding.groupCreateCardViewSceneMember.fadeIn(1000L, 0L)
        }, 900L)

        binding.groupCreateTitleSceneMember.text = getString(R.string.groupCreate_SelectLeader)

        leaderAdapter = LeaderAdapter(object : LeaderAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                dialogHelper.sweetBtnDialog(
                    title = "確認要選擇此隊長嗎？",
                    message = "選擇後將無法更改",
                    cancelable = false,
                    status = SweetAlertDialog.WARNING_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            sendData(AppConfig.URL_CREATE_TEAMMEMBER, mapOf(
                                AppConfig.API_TEAMLEADER_ID to teamList[position].id,
                                AppConfig.API_SID to sharedData.getSid(),
                                AppConfig.API_SNAME to teamList[position].teamName,
                                "User" to "1"
                            ))

                            teamLeaderId = teamList[position].id
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                        }
                    }
                )
            }
        })

        binding.groupCreateRecyclerViewSceneMember.apply {
            val linearLayoutManager = LinearLayoutManager(this@GroupCreateActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = leaderAdapter
        }

        leaderAdapter.differ.submitList(teamList)
    }

    /**
     * 取得隊長
     */
    private fun getLeader() {
        yuuzuApi.api(Request.Method.GET, AppConfig.URL_LIST_TEAMLEADER + "?${AppConfig.API_TEAMDESC_ID}=$teamDescId", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val teamDto = TeamDto(
                            id = jsonObject.getString(AppConfig.API_TEAMLEADER_ID),
                            teamName = jsonObject.getString(AppConfig.API_SNAME),
                            isLeader = true
                        )

                        teamList.add(teamDto)
                    }

                    // setup UI
                    if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()
                    initMemberView()
                    Log.e(AppConfig.TAG, "AppConfig.URL_LIST_TEAMLEADER + \"?${AppConfig.API_TEAMDESC_ID}=$teamDescId")


                } catch (e : JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            theEND()
                            finish()
                        }
                    })
                }
            }

            override fun onError(error: VolleyError) {
                if (error.networkResponse != null) {
                    when(error.networkResponse.statusCode) {
                        406 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_406), "", SweetAlertDialog.ERROR_TYPE, null)}
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "",SweetAlertDialog.ERROR_TYPE, null) }
                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    /**
     * 取得隊長beacon的廣播
     */
    private fun initLeaderBeacon() {
        beaconController = BeaconController(this, Region("Leader", Identifier.parse(AppConfig.BEACON_UUID_LEADER), null, null))
        beaconController.startScanning(object : BeaconController.BeaconModify {
            override fun modifyData(beacons: Collection<Beacon?>?, region: Region?) {
                if (beacons?.isNotEmpty() == true) {
                    countTimer = 0
                    Log.e(AppConfig.TAG, "modifyData: $beacons")

                    val beacon = beacons.first()
                    if (beacon?.id2.toString() == sharedData.getSignID()) {
                        if (beaconController.isScanning()) beaconController.stopScanning()
                        sharedData.saveTeamDescId(beacon?.id3.toString())
                        teamDescId = beacon?.id3.toString()
                        sendData(AppConfig.URL_CREATE_TEAMLEADER,
                            mapOf(
                                AppConfig.API_TEAMDESC_ID to beacon?.id3.toString(),
                                AppConfig.API_SID to sharedData.getSid(),
                                "User" to "1"
                            )
                        )
                    }

                } else {
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

    /**
     * 取得隊員beacon的廣播
     */
    private fun initMemberBeacon() {
        beaconController = BeaconController(this, Region("Member", Identifier.parse(AppConfig.BEACON_UUID_MEMBER), null, null))
        beaconController.startScanning(object : BeaconController.BeaconModify {
            override fun modifyData(beacons: Collection<Beacon?>?, region: Region?) {
                if (beacons?.isNotEmpty() == true) {
                    countTimer = 0

                    val beacon = beacons.first()
                    if (beacon?.id2.toString() == sharedData.getSignID()) {
                        if (beaconController.isScanning()) beaconController.stopScanning()
                        sharedData.saveTeamDescId(beacon?.id3.toString())
                        teamDescId = beacon?.id3.toString()
                        getLeader()
                    }

                } else {
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

    /**
     * 設定Dailog
     */
    private fun initDialog() {
        dialogHelper.initScanningDialog(getString(R.string.groupCreate_WaitingWaiting), object :
            DialogHelper.ScanningPositiveListener {
            override fun onPositiveClick(view: View, dialog: AlertDialog) {
                dialogHelper.sweetBtnDialog(
                    title = getString(R.string.alert_leave_Title),
                    message = getString(R.string.alert_leave_Message),
                    cancelable = false,
                    status = SweetAlertDialog.WARNING_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            theEND()
                            finish()
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                        }
                    }
                )
            }
        })
    }

    /**
     * 設定按鈕
     */
    private fun initButton() {
        binding.groupCreateBtnBack.setOnClickListener {
            dialogHelper.sweetBtnDialog(
                title = "確定要離開嗎？",
                message = "",
                cancelable = false,
                status = SweetAlertDialog.WARNING_TYPE,
                object : DialogHelper.SweetDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                        finish()
                    }

                    override fun onNegativeClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                    }
                }
            )
        }

        binding.groupCreateBtnLeader.setOnClickListener {
            dialogHelper.sweetBtnDialog(
                title = "確定要成爲群組領隊嗎？",
                message = "選擇后將不會能返回！",
                cancelable = false,
                status = SweetAlertDialog.WARNING_TYPE,
                object : DialogHelper.SweetDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                        initLeaderBeacon()
                        if (!dialogHelper.isScanningDialog()) dialogHelper.scanningDialog()
                    }

                    override fun onNegativeClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                    }
                }
            )
        }

        binding.groupCreateBtnMember.setOnClickListener {
            dialogHelper.sweetBtnDialog(
                title = "確定要成爲群組成員嗎？",
                message = "選擇后將不會能返回！",
                cancelable = false,
                status = SweetAlertDialog.WARNING_TYPE,
                object : DialogHelper.SweetDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()

                        binding.groupCreateLinearLayoutScene1.fadeOut(1000L, 0L)
                        Handler(Looper.getMainLooper()).postDelayed({
                            binding.groupCreateLinearLayoutScene1.visibility = View.GONE
                            binding.groupCreateCardViewScene2.visibility = View.VISIBLE
                            binding.groupCreateCardViewScene2.fadeIn(1000L, 0L)
                        }, 900L)

                        initMemberBeacon()
                        if (!dialogHelper.isScanningDialog()) dialogHelper.scanningDialog()
                    }

                    override fun onNegativeClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                    }
                }
            )
        }
    }

    private fun initView() {
        yuuzuApi = YuuzuApi(this)
        sharedData = SharedData(this)
        dialogHelper = DialogHelper(this)

        teamList = ArrayList()
        beaconController = BeaconController(this, Region("Member", Identifier.parse(AppConfig.BEACON_UUID_MEMBER), null, null))
    }

    private fun theEND() {
        isLeadering = false
        isMembering = false
        if (WebSocketManager.isConnect()) WebSocketManager.close()
        if (beaconController.isScanning()) beaconController.stopScanning()
        if (dialogHelper.isScanningDialog()) dialogHelper.dismissScanningDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        theEND()
    }
}
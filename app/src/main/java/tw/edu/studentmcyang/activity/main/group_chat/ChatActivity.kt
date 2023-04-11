package tw.edu.studentmcyang.activity.main.group_chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.firebase.database.*
import org.json.JSONArray
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.databinding.ActivityChatBinding
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData
import tw.edu.studentmcyang.yuuzu_lib.ViewHelper
import tw.edu.studentmcyang.yuuzu_lib.YuuzuApi
import tw.edu.studentmcyang.yuuzu_lib.anim.fadeIn
import tw.edu.studentmcyang.yuuzu_lib.anim.fadeOut
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding

    private lateinit var yuuzuApi: YuuzuApi
    private lateinit var sharedData: SharedData
    private lateinit var viewHelper: ViewHelper
    private lateinit var dialogHelper: DialogHelper

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    private lateinit var groupAdapter: GroupAdapter
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var chatAdapter: ChatAdapter

    private var groupList = ArrayList<GroupDto>()
    private var roomList = ArrayList<RoomDto>()
    private var chatList = ArrayList<ChatDto>()

    private var teamLeaderId = ""
    private var teamDescId = ""
    private var chatRoomId = ""
    private var chatRoomName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initView()
        initButton()
        initScene01()
    }

    /**
     * 設定Firebase，且送出信息
     */
    private fun sendMessage(msg: String) {
        ref = database.getReference(chatRoomId).child(Calendar.getInstance().timeInMillis.toString())
        val message = HashMap<String, String>()
        val datetime = "${Calendar.getInstance().get(Calendar.MONTH) + 1}/${Calendar.getInstance().get(Calendar.DAY_OF_MONTH)} ${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:${Calendar.getInstance().get(Calendar.MINUTE)}"
        message[AppConfig.FIREBASE_USER] = sharedData.getSname()
        message[AppConfig.FIREBASE_MESSAGE] = msg
        message[AppConfig.FIREBASE_DATETIME] = datetime
        ref.setValue(message)
    }

    /**
     * 同步聊天室資料
     */
    private fun syncChat() {
        ref = database.getReference(chatRoomId)
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatData = ArrayList<String>()
                for (messageData in snapshot.children) chatData.add(messageData.value.toString())

                chatList.add(
                    ChatDto(
                        time = chatData[0],
                        message = chatData[1],
                        user = chatData[2],
                        current = snapshot.key.toString()
                    )
                )

                chatAdapter.differ.submitList(chatList)
                binding.groupChatScene3RecyclerView.scrollToPosition(chatList.size - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e(AppConfig.TAG, "onChildChanged: ")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.e(AppConfig.TAG, "onChildRemoved: ")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e(AppConfig.TAG, "onChildMoved: ")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(AppConfig.TAG, "onCancelled: ")
            }
        })
    }

    /**
     * 取得聊天室資料
     */
    private fun getChatRoom() {
        yuuzuApi.api(Request.Method.GET, "${AppConfig.URL_LIST_CHATROOM}?${AppConfig.API_TEAMLEADER_ID}=$teamLeaderId", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        roomList.add(
                            RoomDto(
                            groupChat_id = jsonObject.getString("GroupChat_id"),
                            leaderName = jsonObject.getString("S_name"),
                            chatTitle = jsonObject.getString("ChatTitle"),
                            )
                        )
                    }

                    if (roomList.size == 0)
                        dialogHelper.sweetDialog("目前沒有聊天室", "請等待老師建立聊天室", SweetAlertDialog.WARNING_TYPE, null)

                    roomAdapter.differ.submitList(roomList)

                    if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()

                } catch (e : Exception) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, null)
                }
            }

            override fun onError(error: VolleyError) {
                if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null)}
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "", SweetAlertDialog.ERROR_TYPE, null)}
                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    /**
     * 取得群組資料
     */
    private fun getGroupData(groupDto: GroupDto) {
        yuuzuApi.api(Request.Method.GET, "${AppConfig.URL_LIST_TEAMMEMBER}?${AppConfig.API_TEAMLEADER_ID}=${groupDto.teamLeaderId}&${AppConfig.API_TEAMDESC_ID}=${groupDto.teamDescId}", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    var studentName = ""
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        studentName += "${i + 1}. ${jsonObject.getString(AppConfig.API_SNAME)}\n"
                    }

                    dialogHelper.sweetBtnDialog(
                        title = "是否要進入群組聊天室？",
                        message = "群組成員：$studentName",
                        cancelable = false,
                        status = SweetAlertDialog.WARNING_TYPE,
                        object : DialogHelper.SweetDialogListener {
                            override fun onPositiveClick(dialog: SweetAlertDialog) {
                                dialog.dismiss()
                                teamDescId = groupDto.teamDescId
                                teamLeaderId = groupDto.teamLeaderId

                                initScene02()
                            }

                            override fun onNegativeClick(dialog: SweetAlertDialog) {
                                dialog.dismiss()
                            }
                        }
                    )

                } catch (e: Exception) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, null)
                }
            }

            override fun onError(error: VolleyError) {
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null)}
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "", SweetAlertDialog.ERROR_TYPE, null)}
                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    /**
     * 取得群組列表
     */
    private fun getGroupList() {
        yuuzuApi.api(Request.Method.GET, "${AppConfig.URL_LIST_TEAMDESC}?S_id=${sharedData.getSid()}", object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                val jsonArray = JSONArray(data)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    groupList.add(GroupDto(
                        teamDescId = jsonObject.getString("TeamDesc_id"),
                        teamLeaderId = jsonObject.getString("TeamLeader_id"),
                        teamDoc = jsonObject.getString("Doc"),
                        sid = jsonObject.getString("S_id"),
                        sName = jsonObject.getString("S_name"),
                        isLeader = jsonObject.getString("IsLeader")
                    ))
                }

                groupAdapter.differ.submitList(groupList)
                Log.e(AppConfig.TAG, "onSuccess: $groupList")

                if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
            }

            override fun onError(error: VolleyError) {
                if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        400 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_400), "", SweetAlertDialog.ERROR_TYPE, null)}
                        500 -> {dialogHelper.sweetDialog(getString(R.string.alert_error_500), "", SweetAlertDialog.ERROR_TYPE, null)}
                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    /**
     * 初始化聊天室
     */
    private fun initScene03() {
        syncChat()

        chatAdapter = ChatAdapter(this@ChatActivity)

        binding.groupChatScene3RecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = chatAdapter
        }

        binding.groupChatTitle.text = chatRoomName

        binding.groupChatScene2.fadeOut(500L, 0L)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.groupChatScene2.visibility = View.GONE
            binding.groupChatScene3.fadeIn(500L, 0L)
            binding.groupChatScene3.visibility = View.VISIBLE
        }, 500L)

        binding.groupchatScene3BtnSend.setOnClickListener {
            val text = binding.groupChatTextInput.text.toString()
            if (text.isBlank()) return@setOnClickListener
            sendMessage(text)
            binding.groupChatTextInput.setText("")
        }

        viewHelper.setupUI(binding.groupChatScene3RecyclerView)
    }

    /**
     * 取得聊天室列表
     */
    private fun initScene02() {
        if (!dialogHelper.dialogIsLoading()) dialogHelper.loadingDialog()
        getChatRoom()

        roomAdapter = RoomAdapter(object : RoomAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                dialogHelper.sweetBtnDialog(
                    title = "請問是否要進入此聊天室？",
                    message = "",
                    cancelable = false,
                    status = SweetAlertDialog.WARNING_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            chatRoomId = roomList[position].groupChat_id
                            chatRoomName = roomList[position].chatTitle + "聊天室"
                            initScene03()
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                    }
                }
            )
            }
        })

        binding.groupChatScene2RecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = roomAdapter
        }

        binding.groupChatTitle.text = "選擇聊天室"

        binding.groupChatScene1.fadeOut(500L, 0L)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.groupChatScene1.visibility = View.GONE
            binding.groupChatScene2.fadeIn(500L, 0L)
            binding.groupChatScene2.visibility = View.VISIBLE
        }, 500L)
    }

    /**
     * 取得聊天室列表
     */
    private fun initScene01() {
        dialogHelper.initLoadingDialog("Loading...")
        if (!dialogHelper.dialogIsLoading()) dialogHelper.loadingDialog()

        getGroupList()

        groupAdapter = GroupAdapter(object : GroupAdapter.OnItemClickListener {
            override fun onClick(position: Int) {
                getGroupData(groupList[position])
            }
        })

        binding.groupChatTitle.text = "選擇群組"
        binding.groupChatScene1RecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            hasFixedSize()
            adapter = groupAdapter
        }
    }

    /**
     * 初始化按鈕, 並設定返回鍵
     */
    private fun initButton() {
        binding.groupChatBtnBack.setOnClickListener {
            dialogHelper.sweetBtnDialog(
                title = getString(R.string.alert_leave_Title),
                message = "",
                cancelable = false,
                status = SweetAlertDialog.WARNING_TYPE,
                object : DialogHelper.SweetDialogListener {
                    override fun onPositiveClick(dialog: SweetAlertDialog) {
                        dialog.dismiss()
                        theEnd()
                        finish()
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
        viewHelper = ViewHelper(this)
        dialogHelper = DialogHelper(this)

        database = FirebaseDatabase.getInstance(AppConfig.FIREBASE_URL)

        groupList = ArrayList()
        roomList = ArrayList()
        chatList = ArrayList()
    }

    private fun theEnd() {
        if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        theEnd()
    }
}
package tw.edu.studentmcyang.activity.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.sign.SignActivity
import tw.edu.studentmcyang.yuuzu_lib.BeaconController
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var sharedData: SharedData
    private lateinit var dialogHelper: DialogHelper
    private lateinit var beaconController: BeaconController

    private lateinit var tvName: MaterialTextView
    private lateinit var tvTeacher: MaterialTextView
    private lateinit var tvCourse: MaterialTextView
    private lateinit var btnSign: MaterialButton
    private lateinit var btnAsking: MaterialCardView
    private lateinit var btnLearnHistory: MaterialCardView
    private lateinit var btnLogout: MaterialCardView

    private var isAsking = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: initView()
        tvName = view.findViewById(R.id.home_tvName)
        tvTeacher = view.findViewById(R.id.home_tvTeacher)
        tvCourse = view.findViewById(R.id.home_tvCourse)
        btnSign = view.findViewById(R.id.home_btnSign)
        btnAsking = view.findViewById(R.id.home_btnAsking)
        btnLearnHistory = view.findViewById(R.id.home_btnLearnHistory)
        btnLogout = view.findViewById(R.id.home_btnLogout)

        sharedData = SharedData(requireActivity())
        dialogHelper = DialogHelper(requireActivity())
        beaconController = BeaconController(requireActivity(), Region("Asking", Identifier.parse(AppConfig.BEACON_UUID_MAIN), null, null))

        // TODO: setView()
        tvName.text = sharedData.getSname()
        refreshPage()

        btnLearnHistory.visibility = View.GONE

        //TODO: Function
        initButton()
    }

    /**
     * 刷新頁面
     */
    @SuppressLint("SetTextI18n")
    private fun refreshPage() {
        if (sharedData.getSignID() != "null" && sharedData.getSignID().isNotBlank()) {
            tvTeacher.text = "老師： ${sharedData.getTname()}"
            tvCourse.text = "課程： ${sharedData.getCourseName()}"

            btnSign.text = getString(R.string.home_text_btnLeave)
            btnSign.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.dark_red_secondary_color))
        }
    }

    /**
     * 初始化 Beacon 廣播，用於提醒老師
     */
    private fun initBeaconBroadcast() {
        beaconController.broadcastBeacon(AppConfig.BEACON_UUID_MAIN, sharedData.getSignID(), sharedData.getSid())
        Handler(Looper.getMainLooper()).postDelayed({
            beaconController.startBeaconCasting()
        }, 1000)
    }

    /**
     * 初始化按鈕，判斷是否已經加入課程，並執行相對應的動作
     */
    private fun initButton() {
        btnSign.setOnClickListener{
            if (sharedData.getSignID() != "null" && sharedData.getSignID().isNotBlank()) {
                dialogHelper.sweetBtnDialog(
                    title = getString(R.string.home_text_confirm_Leave),
                    message = "",
                    cancelable = false,
                    status = SweetAlertDialog.WARNING_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            sharedData.quitCourse()

                            tvTeacher.text = getString(R.string.home_text_courseTeacher)
                            tvCourse.text = getString(R.string.home_text_courseNowNone)
                            btnSign.text = getString(R.string.home_text_btnJoin)
                            btnSign.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.dark_blue_secondary_color))

                            dialogHelper.sweetDialog(getString(R.string.alert_finish), "", SweetAlertDialog.SUCCESS_TYPE, null)
                            dialog.dismiss()
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                        }
                    }
                )
            } else {
                requireActivity().startActivity(Intent(requireActivity(), SignActivity::class.java))
            }
        }

        btnAsking.setOnClickListener{
            if (sharedData.getSignID() != "null" && sharedData.getSignID().isNotBlank()) {
                if (!isAsking) {
                    isAsking = true
                    initBeaconBroadcast()
                    Handler(Looper.getMainLooper()).postDelayed({if (beaconController.isBeaconCasting()) beaconController.stopBeaconCasting()}, 10000)
                    Handler(Looper.getMainLooper()).postDelayed({isAsking = false}, 60000)
                    dialogHelper.sweetDialog("消息已送出！", "請耐心等待老師回復。", SweetAlertDialog.SUCCESS_TYPE, null)

                } else {
                    dialogHelper.sweetDialog("您已經送出消息了！", "請耐心等待老師回復。", SweetAlertDialog.WARNING_TYPE, null)
                }
            } else {
                dialogHelper.sweetDialog("請先加入課程！", "", SweetAlertDialog.WARNING_TYPE, null)
            }
        }

//        btnLearnHistory.setOnClickListener{
//            requireActivity().startActivity(Intent(requireActivity(), LearningActivity::class.java))
//        }

        btnLogout.setOnClickListener {
            dialogHelper.sweetDialog(getString(R.string.home_text_btnSignOut), "", SweetAlertDialog.SUCCESS_TYPE, object :
                DialogHelper.SweetPositiveDialogListener {
                override fun onPositiveClick(dialog: SweetAlertDialog) {
                    sharedData.logout()
                    requireActivity().finish()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPage()
    }

    override fun onStop() {
        super.onStop()
        if (beaconController.isBeaconCasting()) beaconController.stopBeaconCasting()
    }

    override fun onPause() {
        super.onPause()
        if (beaconController.isBeaconCasting()) beaconController.stopBeaconCasting()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (beaconController.isBeaconCasting()) beaconController.stopBeaconCasting()
    }
}
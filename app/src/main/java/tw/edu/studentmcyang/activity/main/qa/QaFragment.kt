package tw.edu.studentmcyang.activity.main.qa

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.race.RaceActivity
import tw.edu.studentmcyang.activity.main.sign.SignActivity
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData
import java.util.*

class QaFragment : Fragment(R.layout.fragment_qa) {

    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var scene: ScrollView
    private lateinit var btnQuiz: MaterialCardView
    private lateinit var btnRace: MaterialCardView

    private lateinit var sharedData: SharedData
    private lateinit var dialogHelper: DialogHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: initView()
        shimmer = view.findViewById(R.id.qa_shimmer)
        scene = view.findViewById(R.id.qa_scene)
        btnRace = view.findViewById(R.id.qa_btn_race)
        btnQuiz = view.findViewById(R.id.qa_btn_quiz)

        sharedData = SharedData(requireActivity())
        dialogHelper = DialogHelper(requireActivity())

        // TODO: function()
        showShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            hideShimmer()
        }, 1500)

        initButton()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initButton() {
        btnQuiz.setOnClickListener {
            Log.e(AppConfig.TAG, "initButton: ${sharedData.getSignID()} | ${sharedData.getSignDate()}")
            if (sharedData.getSignID() == "null" || sharedData.getSignID().isBlank()) { // 判斷是否已經簽到
                dialogHelper.sweetDialog(getString(R.string.alert_signFirst), "", SweetAlertDialog.ERROR_TYPE, null)
                return@setOnClickListener
            }

            if (sharedData.getSignDate() == "null" || sharedData.getSignDate().isBlank()) { // 判斷是否已經簽到
                dialogHelper.sweetDialog(getString(R.string.alert_signFirst), "", SweetAlertDialog.ERROR_TYPE, null)
                return@setOnClickListener
            }

            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val current = formatter.format(time)

            if (current != sharedData.getSignDate()) { // 判斷簽到是否是本日簽到的！
                dialogHelper.sweetBtnDialog(
                    title = getString(R.string.alert_signToday),
                    message = "",
                    cancelable = false,
                    status = SweetAlertDialog.ERROR_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            sharedData.quitCourse()
                            requireActivity().startActivity(Intent(requireActivity(), SignActivity::class.java))
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            sharedData.quitCourse()
                        }
                    }
                )
                return@setOnClickListener
            }

            // requireActivity().startActivity(Intent(requireActivity(), QuizActivity::class.java))
        }

        btnRace.setOnClickListener {
            Log.e(AppConfig.TAG, "initButton: ${sharedData.getSignID()} | ${sharedData.getSignDate()}")
            if (sharedData.getSignID() == "null" || sharedData.getSignID().isBlank()) { // 判斷是否已經簽到
                dialogHelper.sweetDialog(getString(R.string.alert_signFirst), "", SweetAlertDialog.ERROR_TYPE, null)
                return@setOnClickListener
            }

            if (sharedData.getSignDate() == "null" || sharedData.getSignDate().isBlank()) { // 判斷是否已經簽到
                dialogHelper.sweetDialog(getString(R.string.alert_signFirst), "", SweetAlertDialog.ERROR_TYPE, null)
                return@setOnClickListener
            }

            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val current = formatter.format(time)

            if (current != sharedData.getSignDate()) { // 判斷簽到是否是本日簽到的！
                dialogHelper.sweetBtnDialog(
                    title = getString(R.string.alert_signToday),
                    message = "",
                    cancelable = false,
                    status = SweetAlertDialog.ERROR_TYPE,
                    object : DialogHelper.SweetDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            sharedData.quitCourse()
                            requireActivity().startActivity(Intent(requireActivity(), SignActivity::class.java))
                        }

                        override fun onNegativeClick(dialog: SweetAlertDialog) {
                            dialog.dismiss()
                            sharedData.quitCourse()
                        }
                    }
                )
                return@setOnClickListener
            }

            requireActivity().startActivity(Intent(requireActivity(), RaceActivity::class.java))
        }
    }

    private fun showShimmer() {
        if (!shimmer.isShimmerStarted) {
            shimmer.startShimmer()
            shimmer.visibility = View.VISIBLE
            scene.visibility = View.GONE
        }
    }

    private fun hideShimmer() {
        if (shimmer.isShimmerStarted) {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
            scene.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        showShimmer()
    }

    override fun onResume() {
        super.onResume()
        showShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            hideShimmer()
        }, 1500)
    }

    override fun onPause() {
        super.onPause()
        hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideShimmer()
    }
}
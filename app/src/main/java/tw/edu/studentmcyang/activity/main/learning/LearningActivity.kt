package tw.edu.studentmcyang.activity.main.learning

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.VolleyError
import org.json.JSONArray
import org.json.JSONException
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.databinding.ActivityLearningBinding
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData
import tw.edu.studentmcyang.yuuzu_lib.YuuzuApi

class LearningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearningBinding

    private lateinit var yuuzuApi: YuuzuApi
    private lateinit var sharedData: SharedData
    private lateinit var dialogHelper: DialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initButton()
    }

    private fun verifyStudent(studentID: String) {
        yuuzuApi.domJudgeAPI(AppConfig.URL_DOM_JUDGE_USERS, object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
                Log.e(AppConfig.TAG, "onSuccess: $data")
                try {
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val userNames = jsonObject.getString("username")
                        if (studentID == userNames) {
                            dialogHelper.sweetDialog("學號驗證成功", "學號：$studentID", SweetAlertDialog.SUCCESS_TYPE, object :
                                DialogHelper.SweetPositiveDialogListener {
                                override fun onPositiveClick(dialog: SweetAlertDialog) {
                                    dialog.dismiss()

                                }
                            })
                        } else {
                            dialogHelper.sweetDialog("學號驗證失敗", "學號：$studentID", SweetAlertDialog.ERROR_TYPE, null)
                        }
                    }
                } catch (e : JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_title_json), "", SweetAlertDialog.ERROR_TYPE, object :
                        DialogHelper.SweetPositiveDialogListener {
                        override fun onPositiveClick(dialog: SweetAlertDialog) {
                            theEnd()
                            finish()
                        }
                    })
                }
            }

            override fun onError(error: VolleyError) {
                if (dialogHelper.dialogIsLoading()) dialogHelper.dismissLoadingDialog()
                if (error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {

                    }
                }
            }

            override val params: Map<String, String>
                get() = mapOf()
        })
    }

    private fun initButton() {
        binding.learningBtnBack.setOnClickListener {
            dialogHelper.sweetBtnDialog(
                getString(R.string.alert_leave_Title),
                "",
                false,
                SweetAlertDialog.WARNING_TYPE,
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
            theEnd()
            finish()
        }

        binding.buttonVerify.setOnClickListener {
            var studentID: String

            binding.editTextStudentId.let {
                if (it.text.isNullOrEmpty()) {
                    dialogHelper.sweetDialog("請輸入學號！", "", SweetAlertDialog.WARNING_TYPE, null)
                    return@setOnClickListener
                }

                if (it.text?.length != 9) {
                    dialogHelper.sweetDialog("學號長度不正確！", "", SweetAlertDialog.WARNING_TYPE, null)
                    return@setOnClickListener
                }

                studentID = it.text.toString()
            }

            verifyStudent(studentID)
            dialogHelper.initLoadingDialog("正在驗證學生身分...")
            dialogHelper.loadingDialog()
        }
    }

    private fun initView() {
        yuuzuApi = YuuzuApi(this)
        sharedData = SharedData(this)
        dialogHelper = DialogHelper(this)
    }

    private fun theEnd() {

    }

    override fun onDestroy() {
        super.onDestroy()
        theEnd()
    }
}
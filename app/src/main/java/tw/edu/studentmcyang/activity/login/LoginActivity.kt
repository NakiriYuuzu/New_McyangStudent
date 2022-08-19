package tw.edu.studentmcyang.activity.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.CheckBox
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.MainActivity
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData
import tw.edu.studentmcyang.yuuzu_lib.ViewHelper
import tw.edu.studentmcyang.yuuzu_lib.YuuzuApi
import tw.edu.studentmcyang.yuuzu_lib.permission.PermissionHelper

class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
    }

    lateinit var yuuzuApi: YuuzuApi
    lateinit var sharedData: SharedData
    lateinit var viewHelper: ViewHelper
    lateinit var dialogHelper: DialogHelper
    lateinit var permissionHelper: PermissionHelper

    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var account: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var loginBtn: MaterialButton
    lateinit var checkBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        initLottie()
        buttonController()
    }

    private fun initView() {
        yuuzuApi = YuuzuApi(this)
        sharedData = SharedData(this)
        viewHelper = ViewHelper(this)
        dialogHelper = DialogHelper(this)
        permissionHelper = PermissionHelper(this)

        lottieAnimationView = findViewById(R.id.login_animation)
        account = findViewById(R.id.login_input_acc)
        password = findViewById(R.id.login_input_pass)
        loginBtn = findViewById(R.id.login_btn_signIn)
        checkBox = findViewById(R.id.login_checkBox_rememberMe)

        viewHelper.setupUI(findViewById(R.id.loginHeader_CardView1))
        viewHelper.setupUI(findViewById(R.id.loginHeader_CardView2))

        permissionHelper.checkALL()

        if (sharedData.getLoginAcc() != "null" && sharedData.getLoginPwd() != "null") {
            account.setText(sharedData.getLoginAcc())
            password.setText(sharedData.getLoginPwd())
            checkBox.isChecked = true
            // FIXME : REMOVE THIS AFTER FINISH DEV
            login()
        }
    }

    private fun initLottie() {
        lottieAnimationView.setAnimation(R.raw.welcome)
        lottieAnimationView.playAnimation()
    }

    private fun buttonController() {
        loginBtn.setOnClickListener {
            login()
        }
    }

    fun login() {
        dialogHelper.loadingDialog()

        val acc = account.text.toString()
        val pass = password.text.toString()

        if (acc.isNotBlank() && acc != "" && pass.isNotBlank() && pass != "") {
            yuuzuApi.api(Request.Method.POST, AppConfig.API_LOGIN, object :
                YuuzuApi.YuuzuApiListener {
                override fun onSuccess(data: String) {
                    val jsonObject = JSONObject(data)
                    val status = jsonObject.get("status").toString()

                    if (status == "true") {
                        val id = jsonObject.get("S_id").toString()
                        val name = jsonObject.get("S_Name").toString()

                        if (checkBox.isChecked) {
                            sharedData.saveLoginAcc(acc)
                            sharedData.saveLoginPwd(pass)
                        }

                        sharedData.saveID(id)
                        sharedData.saveName(name)

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (dialogHelper.dialogIsLoading())
                                dialogHelper.dismissLoadingDialog()

                            Intent(this@LoginActivity, MainActivity::class.java).apply {
                                startActivity(this)
                            }
                        }, 1000)

                    } else {
                        if (dialogHelper.dialogIsLoading())
                            dialogHelper.dismissLoadingDialog()

                        dialogHelper.showDialog(getString(R.string.login_error_wrongInput), "")
                    }
                }

                override fun onError(error: VolleyError) {
                    if (dialogHelper.dialogIsLoading())
                        dialogHelper.dismissLoadingDialog()

                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 400) {
                            dialogHelper.showDialog(getString(R.string.login_error_wrongInput), "")
                        }
                    } else {
                        dialogHelper.showDialog(getString(R.string.login_error_noInternet), "")
                    }
                }

                override val params: Map<String, String>
                    get() = mapOf(
                        "S_Email" to acc,
                        "S_Password" to pass
                    )
            })
        } else {
            if (dialogHelper.dialogIsLoading())
                dialogHelper.dismissLoadingDialog()

            dialogHelper.showDialog(getString(R.string.login_error_empty), "")
        }
    }
}
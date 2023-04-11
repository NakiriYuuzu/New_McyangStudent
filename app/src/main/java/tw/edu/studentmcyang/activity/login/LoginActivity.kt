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
import tw.edu.studentmcyang.activity.MainActivity
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

    /**
     * 初始化設定
     */
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

        dialogHelper.initLoadingDialog("Loading...")

        viewHelper.setupUI(findViewById(R.id.loginHeader_CardView1))
        viewHelper.setupUI(findViewById(R.id.loginHeader_CardView2))

        permissionHelper.checkALL()

        if (sharedData.getLoginAcc() != "null" && sharedData.getLoginPwd() != "null") {
            account.setText(sharedData.getLoginAcc())
            password.setText(sharedData.getLoginPwd())
            checkBox.isChecked = true
            // FIXME : REMOVE THIS AFTER FINISH DEV
            if (AppConfig.DEBUG)
                login()
        }
    }

    /**
     * 初始化動畫
     */
    private fun initLottie() {
        lottieAnimationView.setAnimation(R.raw.welcome)
        lottieAnimationView.playAnimation()
    }

    /**
     * 按鈕監聽
     */
    private fun buttonController() {
        loginBtn.setOnClickListener {
            login()
        }
    }

    /**
     * 登入
     */
    fun login() {
        dialogHelper.loadingDialog()

        val acc = account.text.toString()
        val pass = password.text.toString()

        if (acc.isNotBlank() && acc != "" && pass.isNotBlank() && pass != "") {
            yuuzuApi.api(Request.Method.POST, AppConfig.URL_LOGINS, object :
                YuuzuApi.YuuzuApiListener {
                override fun onSuccess(data: String) {
                    val jsonObject = JSONObject(data)

                    val id = jsonObject.get(AppConfig.API_SID).toString()
                    val name = jsonObject.get(AppConfig.API_SNAME).toString()

                    if (
                        sharedData.getLoginAcc() != "null" && sharedData.getLoginAcc().isNotEmpty()
                        && sharedData.getLoginPwd() != "null" && sharedData.getLoginPwd().isNotEmpty()
                    ) {
                        if (sharedData.getLoginAcc() != acc || sharedData.getLoginPwd() != pass) {
                            sharedData.quitCourse()
                        }
                    }

                    if (checkBox.isChecked) {
                        sharedData.saveLoginAcc(acc)
                        sharedData.saveLoginPwd(pass)
                    }

                    sharedData.saveSid(id)
                    sharedData.saveSname(name)

                    Handler(Looper.getMainLooper()).postDelayed({
                        if (dialogHelper.dialogIsLoading())
                            dialogHelper.dismissLoadingDialog()

                        Intent(this@LoginActivity, MainActivity::class.java).apply {
                            startActivity(this)
                        }
                    }, 1000)
                }

                override fun onError(error: VolleyError) {
                    if (dialogHelper.dialogIsLoading())
                        dialogHelper.dismissLoadingDialog()

                    if (error.networkResponse != null) {
                        if (error.networkResponse.statusCode == 400) {
                            dialogHelper.showDialog(
                                getString(R.string.login_error_wrongInput),
                                error.networkResponse.statusCode.toString()
                            )
                        } else {
                            dialogHelper.showDialog(
                                getString(R.string.alert_error_json),
                                error.networkResponse.statusCode.toString()
                            )
                        }
                    } else {
                        dialogHelper.showDialog(getString(R.string.login_error_noInternet), "")
                    }
                }

                override val params: Map<String, String>
                    get() = mapOf(
                        AppConfig.API_EMAIL to acc,
                        AppConfig.API_PASSWORD to pass
                    )
            })
        } else {
            if (dialogHelper.dialogIsLoading())
                dialogHelper.dismissLoadingDialog()

            dialogHelper.showDialog(getString(R.string.login_error_empty), "")
        }
    }
}
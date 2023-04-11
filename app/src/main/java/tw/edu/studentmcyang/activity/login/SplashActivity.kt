package tw.edu.studentmcyang.activity.login

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.LinearProgressIndicator
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.yuuzu_lib.SharedData

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var totalPage = 0
    private var currentPage = 0
    private val pageList = ArrayList<Int>()

    private lateinit var sharedData: SharedData

    private lateinit var btnNext: MaterialButton
    private lateinit var btnPrev: MaterialButton
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var backgroundImg: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initView()
        initPageList()
        initButton()
    }

    /**
     * 初始化設定
     */
    private fun initView() {
        sharedData = SharedData(this)

        btnNext = findViewById(R.id.splash_btn_Next)
        btnPrev = findViewById(R.id.splash_btn_Prev)
        progressBar = findViewById(R.id.splash_progressBar)
        backgroundImg = findViewById(R.id.splash_backgroundImg)

        if (sharedData.getSplashStatus()) {
            hideALL()
            startActivity(Intent(this, LoginActivity::class.java))

        } else {
            sharedData.saveSplashStatus(true)
            btnPrev.visibility = android.view.View.GONE
        }
    }

    /**
     * 初始化按鈕
     */
    private fun initButton() {
        btnNext.setOnClickListener {
            when (currentPage) {
                totalPage -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                }
                else -> {
                    currentPage++
                    backgroundImg.setImageResource(pageList[currentPage])
                    progressBar.progress = currentPage

                    if (currentPage > 0) {
                        btnPrev.visibility = android.view.View.VISIBLE
                    }
                }
            }

            Log.e("initButton: ", "$currentPage | $totalPage")
        }

        btnPrev.setOnClickListener {
            if (currentPage != 0) {
                currentPage--

                if (currentPage == 0) {
                    btnPrev.visibility = android.view.View.INVISIBLE
                }

                backgroundImg.setImageResource(pageList[currentPage])
                progressBar.progress = currentPage
            }
        }
    }

    private fun hideALL() {
        btnNext.visibility = CheckBox.GONE
        btnPrev.visibility = CheckBox.GONE
        progressBar.visibility = CheckBox.GONE
        backgroundImg.visibility = CheckBox.GONE
    }

    private fun initPageList() {
        pageList.add(R.drawable.student1)
        pageList.add(R.drawable.student2)
        pageList.add(R.drawable.student3)
        pageList.add(R.drawable.student4)
        pageList.add(R.drawable.student5)
        pageList.add(R.drawable.student6)
        pageList.add(R.drawable.student7)

        totalPage = pageList.size - 1
    }
}
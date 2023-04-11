package tw.edu.studentmcyang.activity.main.course

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.VolleyError
import com.facebook.shimmer.ShimmerFrameLayout
import org.json.JSONArray
import org.json.JSONException
import tw.edu.studentmcyang.AppConfig
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.course.adapter.CourseAdapter
import tw.edu.studentmcyang.activity.main.course.model.Course
import tw.edu.studentmcyang.yuuzu_lib.DialogHelper
import tw.edu.studentmcyang.yuuzu_lib.SharedData
import tw.edu.studentmcyang.yuuzu_lib.YuuzuApi

class CourseFragment : Fragment(R.layout.fragment_course) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shimmer: ShimmerFrameLayout

    private lateinit var courseList: ArrayList<Course>
    private lateinit var courseAdapter: CourseAdapter

    private lateinit var sharedData: SharedData
    private lateinit var yuuzuApi: YuuzuApi
    private lateinit var dialogHelper: DialogHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.course_recyclerView)
        shimmer = view.findViewById(R.id.course_ShimmerLayout)

        courseList = ArrayList()

        dialogHelper = DialogHelper(requireActivity())
        sharedData = SharedData(requireActivity())
        yuuzuApi = YuuzuApi(requireActivity())

        getCourse()
        showShimmer()
        initRecyclerView()
    }

    /**
     * 初始化 RecyclerView
     */
    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        courseAdapter = CourseAdapter(courseList)
        recyclerView.adapter = courseAdapter
    }

    /**
     * 取得課程資料
     */
    private fun getCourse() {
        yuuzuApi.api(Request.Method.POST, AppConfig.URL_LIST_COURSE, object :
            YuuzuApi.YuuzuApiListener {
            override fun onSuccess(data: String) {
                try {
                    val jsonArray = JSONArray(data)
                    for (i in 0 until jsonArray.length()) {
                        val course = jsonArray.getJSONObject(i)
                        courseList.add(
                            Course(
                                course.getString("C_name"),
                                course.getInt("C_id"),
                                course.getString("T_name")
                            )
                        )
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            if (courseList.size > 0) {
                                hideShimmer()
                            } else {
                                dialogHelper.sweetDialog(getString(R.string.course_alert_noCourseTitle), getString(R.string.course_alert_noCourseMessage), SweetAlertDialog.WARNING_TYPE, null)
                            }
                        } catch (e: Exception) {
                            dialogHelper.sweetDialog(getString(R.string.course_text_fail2Load), "", SweetAlertDialog.ERROR_TYPE, null)
                        }
                    }, 1250)

                } catch (e: JSONException) {
                    dialogHelper.sweetDialog(getString(R.string.alert_error_json), "", SweetAlertDialog.ERROR_TYPE, null)
                }
            }

            override fun onError(error: VolleyError) {
                // Crash show UI Change it to MainActivity [When No Server Side!]
            }

            override val params: Map<String, String>
                get() = mapOf(
                    AppConfig.API_SID to sharedData.getSid()
                )
        })
    }

    /**
     * 顯示 Shimmer
     */
    private fun showShimmer() {
        if (!shimmer.isShimmerStarted) {
            shimmer.startShimmer()
            shimmer.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }
    }

    /**
     * 隱藏 Shimmer
     */
    private fun hideShimmer() {
        if (shimmer.isShimmerStarted) {
            shimmer.stopShimmer()
            shimmer.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        showShimmer()
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
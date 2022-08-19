package tw.edu.studentmcyang.activity.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.yuuzu_lib.SharedData

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var sharedData: SharedData

    private lateinit var tvName: MaterialTextView
    private lateinit var tvTeacher: MaterialTextView
    private lateinit var tvCourse: MaterialTextView
    private lateinit var btnCourse: MaterialButton
    private lateinit var btnLearnHistory: MaterialCardView
    private lateinit var btnLogout: MaterialCardView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: initView()
        tvName = view.findViewById(R.id.home_tvName)
        tvTeacher = view.findViewById(R.id.home_tvTeacher)
        tvCourse = view.findViewById(R.id.home_tvCourse)
        btnCourse = view.findViewById(R.id.home_btnCourse)
        btnLearnHistory = view.findViewById(R.id.home_btnLearnHistory)
        btnLogout = view.findViewById(R.id.home_btnLogout)

        sharedData = SharedData(requireActivity())

        // TODO: setView()
        tvName.text = sharedData.getName()

        //TODO: Function
        initButton()
    }

    private fun initButton() {
        btnCourse.setOnClickListener{

        }

        btnLearnHistory.setOnClickListener{

        }

        btnLogout.setOnClickListener {
            sharedData.logout()
            requireActivity().finish()
        }
    }
}
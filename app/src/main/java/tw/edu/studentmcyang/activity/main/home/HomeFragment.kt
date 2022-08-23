package tw.edu.studentmcyang.activity.main.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.sign.SignActivity
import tw.edu.studentmcyang.yuuzu_lib.SharedData

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var sharedData: SharedData

    private lateinit var tvName: MaterialTextView
    private lateinit var tvTeacher: MaterialTextView
    private lateinit var tvCourse: MaterialTextView
    private lateinit var btnSign: MaterialButton
    private lateinit var btnAsking: MaterialCardView
    private lateinit var btnLearnHistory: MaterialCardView
    private lateinit var btnLogout: MaterialCardView

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

        // TODO: setView()
        tvName.text = sharedData.getName()

        //TODO: Function
        initButton()
    }

    private fun initButton() {
        btnSign.setOnClickListener{
             requireActivity().startActivity(Intent(requireActivity(), SignActivity::class.java))
        }

        btnAsking.setOnClickListener{
            // requireActivity().startActivity(Intent())n
        }

        btnLearnHistory.setOnClickListener{
            // requireActivity().startActivity(Intent())
        }

        btnLogout.setOnClickListener {
            sharedData.logout()
            requireActivity().finish()
        }
    }
}
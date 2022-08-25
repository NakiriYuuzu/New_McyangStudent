package tw.edu.studentmcyang.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import tw.edu.studentmcyang.R

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navHostFragment: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        navHostFragment = findViewById(R.id.fragment)
        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
    }
}
package tw.edu.studentmcyang.activity.main.qa

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import tw.edu.studentmcyang.R

class QaFragment : Fragment(R.layout.fragment_qa) {

    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var scene: ScrollView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: initView()
        shimmer = view.findViewById(R.id.qa_shimmer)
        scene = view.findViewById(R.id.qa_scene)

        // TODO: function()
        showShimmer()
        Handler(Looper.getMainLooper()).postDelayed({
            hideShimmer()
        }, 1500)

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

    override fun onPause() {
        super.onPause()
        hideShimmer()
    }

}
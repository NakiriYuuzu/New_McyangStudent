package tw.edu.studentmcyang.activity.main.course.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import tw.edu.studentmcyang.R
import tw.edu.studentmcyang.activity.main.course.model.Course

class CourseAdapter(
    private val courseList: ArrayList<Course>
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.rvlayout_course,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        if (course.C_Name.length > 12) {
            holder.courseName.text = course.C_Name.substring(0, 12) + "..."
        } else {
            holder.courseName.text = course.C_Name
        }
        holder.teacherName.text = course.T_Name + "老師"
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(courseList: ArrayList<Course>) {
        this.courseList.clear()
        this.courseList.addAll(courseList)
        notifyDataSetChanged()
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseName: MaterialTextView = itemView.findViewById(R.id.rvlayout_text_Course)
        val teacherName: MaterialTextView = itemView.findViewById(R.id.rvlayout_text_Teacher)
    }
}
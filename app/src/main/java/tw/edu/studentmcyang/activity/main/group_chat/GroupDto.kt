package tw.edu.studentmcyang.activity.main.group_chat

data class GroupDto(
    val teamDescId: String,
    val teamLeaderId: String,
    val teamDoc: String,
    val sid: String,
    val sName: String,
    val isLeader: String
)
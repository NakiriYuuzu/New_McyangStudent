package tw.edu.studentmcyang.yuuzu_lib.model

interface MessageListener {
    fun  onConnectSuccess () // successfully connected
    fun  onConnectFailed () // connection failed
    fun  onClose () // close
    fun onMessage(text: String?)
}
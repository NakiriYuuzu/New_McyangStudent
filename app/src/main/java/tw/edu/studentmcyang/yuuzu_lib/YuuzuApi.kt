package tw.edu.studentmcyang.yuuzu_lib

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.nio.charset.StandardCharsets

class YuuzuApi(ctx: Context) {
    private val requestQueue: RequestQueue

    init {
        requestQueue = Volley.newRequestQueue(ctx)
    }

    fun api(method: Int, url: String, yuuzuApiListener: YuuzuApiListener) {
        val request: StringRequest = object : StringRequest(method, url, { response ->

            val result = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)

            yuuzuApiListener.onSuccess(result)

        }, { error ->
            yuuzuApiListener.onError(error)
        }) {
            override fun getParams(): Map<String, String> {
                return yuuzuApiListener.params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun domJudgeAPI(url: String, yuuzuApiListener: YuuzuApiListener) {
        val request: StringRequest = object : StringRequest(Method.GET, url, { response ->
            val result = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            yuuzuApiListener.onSuccess(result)
        }, { error ->
            yuuzuApiListener.onError(error)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Basic czEwODI5Nzg6czEwODI5Nzg="
                return headers
            }

            override fun getParams(): Map<String, String> {
                return yuuzuApiListener.params
            }
        }

        requestQueue.add(request)
    }

    fun flushAPI() {
        requestQueue.cancelAll { true }
        requestQueue.stop()
    }

    interface YuuzuApiListener {
        fun onSuccess(data: String)
        fun onError(error: VolleyError)
        val params: Map<String, String>
    }
}
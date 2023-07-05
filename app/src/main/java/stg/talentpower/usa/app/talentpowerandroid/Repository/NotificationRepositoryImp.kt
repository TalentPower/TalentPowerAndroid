package stg.talentpower.usa.app.talentpowerandroid.Repository

import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import stg.talentpower.usa.app.talentpowerandroid.Util.FCMConstants

class NotificationRepositoryImp (private val database: FirebaseFirestore, private val volley:RequestQueue):NotificationRepository{

    override fun addTopic() {
        TODO("Not yet implemented")
    }

    override fun getTopics() {
        TODO("Not yet implemented")
    }

    override fun getTopic() {
        TODO("Not yet implemented")
    }

    override fun makeNotification(notification:JSONObject) {
        val request :JsonObjectRequest = object : JsonObjectRequest(Method.POST,
            FCMConstants.FCM_API,
            notification,
            Response.Listener { response ->
                Log.e("tag", response.toString())
            },
            Response.ErrorListener{ error ->
                Log.e("tag", error.message.toString() )
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = FCMConstants.serverKey
                headers["Content-Type"] = FCMConstants.contentType
                return headers
            }
        }
        volley.add(request)
    }


}
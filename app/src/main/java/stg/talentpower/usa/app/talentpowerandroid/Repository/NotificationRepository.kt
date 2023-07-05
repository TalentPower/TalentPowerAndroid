package stg.talentpower.usa.app.talentpowerandroid.Repository

import org.json.JSONObject

interface NotificationRepository {
    fun addTopic()
    fun getTopics()
    fun getTopic()
    fun makeNotification(notification: JSONObject)
}
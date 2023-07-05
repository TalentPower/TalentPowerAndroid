package stg.talentpower.usa.app.talentpowerandroid.Model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Stop(
    var name: String="",
    var address:String="",
    var location: GeoPoint?=null,
    var time:String="",
    var workers:List<String>? = mutableListOf(),
    @ServerTimestamp
    var createdItem: Date?=null,
    @ServerTimestamp
    var lastupdated: Date?=null,

)

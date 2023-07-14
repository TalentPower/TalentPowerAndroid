package stg.talentpower.usa.app.talentpowerandroid.Model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Route(
    var name:String="",
    var driver:String?="",
    var idDriver:String="",
    var client:String="",
    var noRequWorkers:Int=0,
    var noReclWorkers:Int=0,
    var noRealWorkers:Int=0,
    //var startPoint:GeoPoint?=null,
    //var timeStart:String="",
    //var endPoint:GeoPoint?=null,
    //var timeEnd:String="",
    var timeTotal:String="",
    var kmTotal:Double=0.0,
    var status:String="",
    @ServerTimestamp
    var createdItem:Date?=null,
    @ServerTimestamp
    var lastupdated:Date?=null,
)



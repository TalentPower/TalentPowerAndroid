package stg.talentpower.usa.app.talentpowerandroid.Model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Worker(
    var tokenFCM:String="",
    var rfc:String="",
    var name:String="",
    var phone:String="",
    var noNomina:String="",
    var workedDays:Int=0,
    var availableFacture:Boolean=false,
    var asignedStop:String="",
    var asignedRoute:String="",
    var asignedClient:String="",
    var area:String="",
    var rol:String="worker",
    @ServerTimestamp
    var createdItem: Date?=null,
)

package stg.talentpower.usa.app.talentpowerandroid.Model

import com.google.firebase.firestore.GeoPoint

data class Client(
    var name:String="",
    var address:String="",
    var latlng: GeoPoint?=null,
    var facturation:Int=0,
    var areas:MutableList<String>?= mutableListOf()
){
    override fun toString(): String {
        return name
    }
}
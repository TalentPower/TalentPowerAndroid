package stg.talentpower.usa.app.talentpowerandroid.Model

import com.google.firebase.firestore.GeoPoint

data class EmployeeClient(
    var tokenFCM:String="",
    var id:String="",
    var name:String="",
    var clientAsigned:String="",
    var phone:String="",
    var image:String="",
    var rol:String="employeeClient",
    var email:String="",
    var password:String=""
)

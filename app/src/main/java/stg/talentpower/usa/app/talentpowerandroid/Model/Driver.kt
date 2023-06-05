package stg.talentpower.usa.app.talentpowerandroid.Model

data class Driver(
    var id:String="",
    var Name: String = "",
    var Address: String = "",
    var Phone: String = "",
    var EmergencyPhone: String = "",
    var NSS:String="",
    var BankKey:String="",
    var Email:String="",
    var Password:String="",
    var Rol:String="Driver",
    var Route:String="",
    val images: List<String> = arrayListOf()
)
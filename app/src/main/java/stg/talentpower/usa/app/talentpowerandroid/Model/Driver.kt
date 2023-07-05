package stg.talentpower.usa.app.talentpowerandroid.Model

data class Driver(
    var tokenFCM:String="",
    var id:String="",
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var emergencyPhone: String = "",
    var NSS:String="",
    var bankKey:String="",
    var rol:String="driver",
    var route:String="",
    val images: MutableList<ImagesModel> = arrayListOf()
)

data class Driver2(
    var tokenFCM:String="",
    var id:String="",
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    var emergencyPhone: String = "",
    var NSS:String="",
    var bankKey:String="",
    var rol:String="driver",
    var route:String="",
    val images: MutableList<ImagesModel2> = arrayListOf()
){

    override fun toString(): String {
        return name
    }


}
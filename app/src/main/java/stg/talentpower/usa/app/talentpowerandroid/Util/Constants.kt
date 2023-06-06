package stg.talentpower.usa.app.talentpowerandroid.Util


object FireStoreCollection{
    val NOTE = "note"
    val USERS = "users"
    val EMPLOYESS="employess"
    val CLIENTS="clients"
    val DRIVERS="drivers"
}

object FireDatabase{
    val TASK = "task"
}

object FireStoreDocumentField {
    val DATE = "date"
    val USER_ID = "user_id"
}

object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
}

object FirebaseStorageConstants {
    val ROOT_DIRECTORY = "app"
    val NOTE_IMAGES = "note"
}

object UploadImagesDriverConstants{
    val LICENCIA="licencia"
    val RFC="rfc"
    val ACTA_NACIMIENTO="acta_nacimiento"
    val CERT_ESTUDIOS="cert_estudios"
    val NSS="nss"
    val COMP_DOMICILIO="comp_domicilio"
    val SOLI_EMPLEO="soli_empleo"
    val INE="ine"
    val CURP="curp"
}

enum class HomeTabs(val index: Int, val key: String) {
    NOTES(0, "notes"),
    TASKS(1, "tasks"),
}

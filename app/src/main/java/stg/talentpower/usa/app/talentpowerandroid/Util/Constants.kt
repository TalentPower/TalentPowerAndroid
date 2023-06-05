package stg.talentpower.usa.app.talentpowerandroid.Util


object FireStoreCollection{
    val NOTE = "note"
    val USERS = "users"
    val EMPLOYESS="employess"
    val CLIENT="client"
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

enum class HomeTabs(val index: Int, val key: String) {
    NOTES(0, "notes"),
    TASKS(1, "tasks"),
}

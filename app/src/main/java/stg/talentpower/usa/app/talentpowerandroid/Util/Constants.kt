package stg.talentpower.usa.app.talentpowerandroid.Util

object FireStoreCollection{
    val EMPLOYESS="employess"
    val CLIENTS="clients"
    val DRIVERS="drivers"
    val ROUTES="routes"
    val WORKERS="workers"
    val EMPLOYEES_CLIENT="employees_client"
}

object WorkersArea{
    val AREA="area"
    val OPERADOR_DE_PRODUCCION="operador_de_produccion"
}

object Cities{
    val QUERETARO="queretaro"
}

object EmployessAreas{
    val SISTEMAS="sistemas"
    val RECLUTAMIENTO="reclutamiento"
    val CONTABILIDAD="contabilidad"
    val VENTAS="ventas"
    val JURIDICO="juridico"
    val DIRECCION="direccion"
    val DPTO="department"
}

object Country{
    val CANADA="canada"
    val MEXICO="mexico"
    val USA="usa"
}

object FireStoreDocumentSubcolection {
    val STOPS="stops"
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

object RouteStatus{
    const val CREADA="creada"
    const val FINALIZADA="finalizada"
    const val INICIADA="iniciada"
}

object Topics{
    const val STARTED_ROUTE="started_route"
    const val ENDED_ROUTE="ended_route"
    const val DELAYED_ROUTE="delayed_route"
    const val TOTAL_PASSENGER="total_passenger"
    const val WORKER_FACTURATION="worker_facturation"
}

object FCMConstants{
    const val FCM_API = "https://fcm.googleapis.com/fcm/send"
    const val serverKey =
        "key=" + "AAAAxo4Eyjg:APA91bGsI3S9jmN5yK81xpTbwdxBPx2DGSHvJAcJPWxQfRg2yecQmAuwEqtEBQBF3u8AF-mXYOWxZFTFnkD2gnL__PdB8PxDm_T4jpY733RwoYGtxQHILq4Lub_oZmfVQt3bPYzI3QCE"
    const val contentType = "application/json"
}

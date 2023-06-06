package stg.talentpower.usa.app.talentpowerandroid.Model

import android.net.Uri

data class ImagesModel(
    var Image: Uri? =null,
    var Name:String?=""
)

data class MapImages(
    var ListImages:List<ImagesModel>
)

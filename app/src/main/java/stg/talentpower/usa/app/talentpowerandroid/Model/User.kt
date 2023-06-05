package stg.talentpower.usa.app.talentpowerandroid.Model

sealed class UserType<out T>{
    data class getUser<out T>(val data: T): UserType<T>()

}

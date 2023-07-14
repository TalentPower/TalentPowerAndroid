package stg.talentpower.usa.app.talentpowerandroid.Util

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round


fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun View.disable(){
    isEnabled = false
}

fun View.enabled(){
    isEnabled = true
}

fun Fragment.toast(msg: String?){
    Toast.makeText(requireContext(),msg, Toast.LENGTH_LONG).show()
}

fun Activity.toast(msg: String?){
    Toast.makeText(this,msg, Toast.LENGTH_LONG).show()

}

fun Context.createDialog(layout: Int, cancelable: Boolean): Dialog {
    val dialog = Dialog(this, R.style.Theme_Dialog)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(layout)
    dialog.window?.setGravity(Gravity.CENTER)
    dialog.window?.setLayout(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(cancelable)
    return dialog
}
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

object Date{
    fun currentData():String{
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss", Locale.getDefault())
        val now = Date()
        return formatter.format(now)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun currentDataObject(): LocalDateTime? {
        val calendar = Calendar.getInstance()

        return LocalDateTime.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }
}

object TimePickerHelper{
    val mcurrentTime = Calendar.getInstance()
    val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
    val minute = mcurrentTime.get(Calendar.MINUTE)
    fun returnTime(hourOfDay:Int,minute:Int):String{
        var am_pm:String=""
        var hr=hourOfDay

        if (hr==0){
            hr=12
            am_pm="AM"
        }else{
            if (hr<12){
                am_pm="AM"

            }else{
                if (hr==12){
                    am_pm="PM"
                }else{
                    if (hr>12){
                        hr=hourOfDay-12
                        am_pm = "PM"
                    }
                }
            }
        }
        val hourStr = if (hr < 10) "0${hr}" else "${hr}"
        val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
        return "${hourStr}:${minuteStr} $am_pm"
    }
}



fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}
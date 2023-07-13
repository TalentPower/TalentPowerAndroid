package stg.talentpower.usa.app.talentpowerandroid.Util

import stg.talentpower.usa.app.talentpowerandroid.Model.DateTime

class Util {

    object DateHelper{
        fun returnTimeCasted(hourOfDay:Int,minute:Int): DateTime {
            val date=DateTime()
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
            val hourStr = hr.toString()
            val minuteStr = if (minute < 10) "0${minute}" else "$minute"
            date.hour=hourStr
            date.minute=minuteStr
            date.period=am_pm
            return date
        }
    }
}
package icu.bluedream.nuntius.app.util

import icu.bluedream.nuntius.app.config.Configuration
import java.util.Calendar
import java.util.Date

open class AutoCheckedKey(val configKey: String, val isDailyCheckedKey: Boolean = true) {
    val shouldFetchFromNetwork: Boolean
        get() {
            val lastDate = Date(Configuration.getConfiguration().getValue(configKey, "0").toLong())
            return if (isDailyCheckedKey) {
                !isToday(lastDate)
            } else {
                val diffInDays = (Date().time - lastDate.time) / (1000 * 60 * 60 * 24)
                diffInDays >= 5
            }
        }

    fun storeFetch(date: Date) {
        Configuration.getConfiguration().setValue(configKey, date.time.toString())
    }

    companion object {
        private fun isToday(date: Date): Boolean {
            val today = Calendar.getInstance()
            val thatDay = Calendar.getInstance().apply { time = date }
            return today.get(Calendar.YEAR) == thatDay.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == thatDay.get(Calendar.DAY_OF_YEAR)
        }
    }
}
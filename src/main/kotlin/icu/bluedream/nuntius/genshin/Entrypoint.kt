package icu.bluedream.nuntius.genshin

import icu.bluedream.nuntius.genshin.app.database.DatabaseHelper
import icu.bluedream.nuntius.genshin.impl.DeviceEnv

object GenshinEntrypoint {
    fun beforeStart() {
        DeviceEnv.checkDeviceEnv()
        DatabaseHelper
    }
}
package app.xash.upisample

import android.content.Context
import com.icici.ultrasdk.Models.Accounts
import com.icici.ultrasdk.SDKManager
import java.util.*

object SDKMan {
    private var INSTANCE: SDKManager? = null
    private var accountList = arrayListOf<Accounts>()
    private var vpaList = arrayListOf<String>()

    fun initialize(context: Context, subId: String, number: String) {
        INSTANCE = SDKManager.getSDKManager(
            context.applicationContext,
            "9582054664", // from flutter
            "com.xash.tech",
            "84521654864135", // ANDROID_ID or IMEI, for now harcode
            subId,
            "OPERATOR NAME", // can be null (can come from telephone)
            false,
            "XCHANGE", //correct
            "GLGOxLn2D8ARCBdyLHkRLMdtAPhLAwWI"
        )
    }

    private fun getSDK() = checkNotNull(INSTANCE, { "SDKM.initialize() not called" })


    fun getSeqNumber(): String {
        return "ICI" + java.lang.String.valueOf(UUID.randomUUID())
            .replace("[\\s\\-()]".toRegex(), "")
    }

    fun setAccountList(accounts: ArrayList<Accounts>) {
        accountList = accounts
        vpaList.clear()
        accounts.forEach {
            vpaList.add(it.va)
        }
    }

    fun getVpaList() = vpaList

    val sdk get() = getSDK()

}
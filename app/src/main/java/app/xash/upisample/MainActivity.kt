package app.xash.upisample

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.ListAccounsReq
import com.icici.ultrasdk.RequestModels.PayToVirtualAccountReq
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback

class MainActivity : AppCompatActivity(), SDKMessageCallback {
    lateinit var sdkManager: SDKManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSDK()
    }

    @SuppressLint("MissingPermission")
    fun initSDK () {
        val subscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0)
        val subId = subInfo.subscriptionId
        sdkManager = SDKManager.getSDKManager(
                this,
                "+918800233266",
                "com.xash.tech",
                "84521654123456",
            subId.toString(),
                "OPERATOR NAME", // can be null
        false,
                "IMobile",
                "GLGOxLn2D8ARCBdyLHkRLMdtAPhLAwWI"
        )
        sdkManager.setSdkMessageCallback(this)
        val cb = object: UltraSDKCallBack {
            override fun onResponse(p0: USDKResponse?) {
                Toast.makeText(this@MainActivity, "SUCCESS", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(p0: USDKResponse?, p1: Throwable?) {
                Log.e("UPI", "FAIL", p1)
                Toast.makeText(this@MainActivity, "FAIL", Toast.LENGTH_SHORT).show()
            }

        }
        sdkManager.invokeSDK("kghkdsghsjglsgk", "+919643339706", cb)
    }

    override fun nextMsgForProgress(p0: String?) {
        Log.d("UPI", "nextMsgForProgress : $p0")
    }

    override fun toggleDeviceBindingBackGroundFail() {
        Log.w("UPI", "toggleDeviceBindingBackGroundFail")
    }

}
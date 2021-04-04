package app.xash.upisample

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.ErrorCodes.ErrorCodes
import com.icici.ultrasdk.ErrorCodes.RequestCodes
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.GetProfileIdReq
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback

class MainActivity : AppCompatActivity(), SDKMessageCallback, UltraSDKCallBack {
    lateinit var sdkManager: SDKManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initSDK()
    }

    @SuppressLint("MissingPermission")
    fun initSDK() {
        val subscriptionManager =
            getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0)
        val subId = subInfo.subscriptionId
        sdkManager = SDKManager.getSDKManager(
            this,
            "8595352647", // from flutter
            "com.xash.tech",
            "84521654864135", // ANDROID_ID or IMEI, for now harcode
            subId.toString(),
            "OPERATOR NAME", // can be null (can come from telephone)
            false,
            "XCHANGE", //correct
            "xPiJGRzowAVe0rU5X2mKNEu3J7ZF3gHs" // correct
        )
        sdkManager.setSdkMessageCallback(this)


        sdkManager.invokeSDK(
            "XCH", // correct
            "+919282123345", // VMN (providing soon)
            this
        )

        // TODO: sdkManager.payToVPA()
        // TODO: sdkManager.storeAccountDetails() - create account
    }

    override fun nextMsgForProgress(p0: String?) {
        Log.d("UPI", "nextMsgForProgress : $p0")
    }

    override fun toggleDeviceBindingBackGroundFail() {
        Log.w("UPI", "toggleDeviceBindingBackGroundFail")
    }

    override fun onResponse(response: USDKResponse) {
        Log.e("RESPONSE", "$response")
        Log.e("RESPONSE", "${RequestCodes.AUTHENTICATION.requestCode}")
        val req = GetProfileIdReq()
        val reqCode = RequestCodes.GET_PROFILE_ID.requestCode
        sdkManager.getProfileId(req, reqCode, this);

        if (response.reqCode.equals(RequestCodes.
            AUTHENTICATION.requestCode)) {
            if (response.response.equals(ErrorCodes.U200.errorCode)) {
                //calling service to get profile Id
                val req = GetProfileIdReq()
                val reqCode = RequestCodes.GET_PROFILE_ID.requestCode
                sdkManager.getProfileId(req, reqCode, this);
                Toast.makeText(this, response.response, Toast.LENGTH_SHORT).show()
            }
        } else if (response.reqCode.equals(RequestCodes.SEND_SMS.requestCode, true)) {
            if (response.response.equals("SMS Success", true)) {
                Log.e("INTITATE", "$response")
                sdkManager.requestForDeviceBinding()

                Toast.makeText(this, response.response, Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onFailure(p0: USDKResponse?, p1: Throwable?) {
        Log.e("UPI", "FAIL :    ${p0?.message} ", p1)
        Toast.makeText(this@MainActivity, "${p0?.message}", Toast.LENGTH_SHORT).show()
    }

}
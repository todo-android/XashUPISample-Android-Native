package app.xash.upisample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.ErrorCodes.ErrorCodes
import com.icici.ultrasdk.ErrorCodes.RequestCodes
import com.icici.ultrasdk.Models.Accounts
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.GetProfileDetailsReq
import com.icici.ultrasdk.RequestModels.GetProfileIdReq
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback


class MainActivity : AppCompatActivity(), SDKMessageCallback, UltraSDKCallBack {
    lateinit var sdkManager: SDKManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissionForSms()
        initSDK()
    }

    private fun checkPermissionForSms() {

    }

    @SuppressLint("MissingPermission")
    fun initSDK() {
        val subscriptionManager =
            getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1)
        val subId = subInfo.subscriptionId
        SDKMan.initialize(this, subId.toString(), subInfo.number)
        sdkManager = SDKMan.sdk
        sdkManager.setSdkMessageCallback(this)
        sdkManager.invokeSDK(
            "XCH TRB", // correct
            "+919282123345", // VMN (providing soon)
            this
        )
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

        if (response.reqCode.equals(
                RequestCodes.AUTHENTICATION.requestCode
            )
        ) {
            if (response.response.equals(ErrorCodes.U200.errorCode)) {
                //calling service to get profile Id
                val req = GetProfileIdReq()
                val reqCode = RequestCodes.GET_PROFILE_ID.requestCode
                sdkManager.getProfileId(req, reqCode, this)
//                if(response.response.equals("Authentication done", true)){
//                    startActivity(Intent(this, PayVPActivity::class.java))
//                    finish()
//                }
            }
        } else if (response.reqCode.equals(RequestCodes.GET_PROFILE_ID.requestCode, true)) {
            if (response.response.equals("0") && response.userProfile != null &&
                !response.userProfile.equals("")
            ) {
                val profileId = response.userProfile
                //calling service to get profile details - gives existing list of accounts and VPAs tagged to them.
                val req = GetProfileDetailsReq()
                val reqCode = RequestCodes.GET_PROFILE_DETAILS.requestCode
                req.profileId = profileId
                sdkManager.getProfileDetails(req, reqCode, this)
            } else if (response.response.equals("1") && response.userProfile.isNullOrEmpty()) {
                startActivity(Intent(this, MainActivity2::class.java))
                finish()
            }
        } else if (response.reqCode.equals(RequestCodes.GET_PROFILE_DETAILS.requestCode, true)) {
            if (response.response
                    .equals("0") && response.mobileAppData != null && response.mobileAppData
                    .details != null &&
                response.mobileAppData.details.accounts != null
            ) {
                //calling service to get profile details - gives existing list of accounts and VPAs tagged to them.
                SDKMan.setAccountList(response.mobileAppData.details.accounts as ArrayList<Accounts>)
                startActivity(Intent(this, PayVPActivity::class.java))
                finish()
            }
        } else if (response.reqCode.equals(RequestCodes.SEND_SMS.requestCode, true)) {
            if (response.response.equals("SMS Success", true)) {
                Log.e("INTITATE", "$response")
                sdkManager.requestForDeviceBinding()

                Toast.makeText(this, response.response, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFailure(p0: USDKResponse?, p1: Throwable?) {
        Log.e("UPI", "FAIL :    ${p0?.message} ", p1)
        Toast.makeText(this@MainActivity, "${p0?.message}", Toast.LENGTH_SHORT).show()
    }

}
package app.xash.upisample.ui.devicebinding

import android.content.Context
import android.telephony.SubscriptionInfo
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.xash.upisample.SDKMan
import app.xash.upisample.modal.ResponseState
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.ErrorCodes.ErrorCodes
import com.icici.ultrasdk.ErrorCodes.RequestCodes
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.GetProfileDetailsReq
import com.icici.ultrasdk.RequestModels.GetProfileIdReq
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback

class DeviceBindingViewModel : ViewModel(), UltraSDKCallBack, SDKMessageCallback {
    private lateinit var sdkManager: SDKManager
    private val errorLiveData = MutableLiveData<String>()
    private val responseLiveData = MutableLiveData<ResponseState.Success>()
    val result = MediatorLiveData<ResponseState>()

    init {
        result.addSource(errorLiveData) {
            result.value = ResponseState.Error(it)
        }
        result.addSource(responseLiveData) {
            result.value = it
        }
    }

    fun initialize(context: Context, subInfo: SubscriptionInfo) {
        SDKMan.initialize(context, subInfo.subscriptionId.toString(), subInfo.number)
        sdkManager = SDKMan.sdk
        sdkManager.setSdkMessageCallback(this)
        sdkManager.invokeSDK(
            "XCH TRB", // correct
            "+919282123345", // VMN (providing soon)
            this
        )
    }


    override fun onResponse(res: USDKResponse) {
        Log.e("UPI", "Response Success : $res")
        if (res.reqCode.equals(RequestCodes.SEND_SMS.requestCode, true)) {
            if (res.response.equals("SMS Success", true)) {
                sdkManager.requestForDeviceBinding()
            }
        } else if (res.reqCode.equals(RequestCodes.AUTHENTICATION.requestCode)) {
            if (res.response.equals(ErrorCodes.U200.errorCode)) {
                val req = GetProfileIdReq()
                val reqCode = RequestCodes.GET_PROFILE_ID.requestCode
                sdkManager.getProfileId(req, reqCode, this)
            }
        } else if (res.reqCode.equals(RequestCodes.GET_PROFILE_ID.requestCode, true)) {
            if (res.response.equals("1") && res.userProfile.isNullOrEmpty()) {
                responseLiveData.value = ResponseState.Success(
                    RequestCodes.GET_PROFILE_ID.requestCode,
                    "No Profile Found"
                )
            } else if (res.response.equals("0") && !res.userProfile.isNullOrEmpty()) {
                val profileId = res.userProfile
                val req = GetProfileDetailsReq()
                val reqCode = RequestCodes.GET_PROFILE_DETAILS.requestCode
                req.profileId = profileId
                sdkManager.getProfileDetails(req, reqCode, this)
            }
        }
    }

    override fun onFailure(res: USDKResponse?, err: Throwable?) {
        Log.e("UPI", "Response Failure : $res")
        errorLiveData.value = res?.message
    }

    override fun nextMsgForProgress(msg: String) {
        Log.d("UPI", "nextMsgForProgress : $msg")
        responseLiveData.value = ResponseState.Success("BINDING", msg)
    }

    override fun toggleDeviceBindingBackGroundFail() {
        Log.e("UPI", "Device BindingBackgroundFail")
        errorLiveData.value = "Device Binding Background Fail"
    }
}
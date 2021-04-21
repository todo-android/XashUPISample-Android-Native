package app.xash.upisample

import android.content.Context
import android.telephony.SubscriptionInfo
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback

class DeviceBindingViewModel : ViewModel(), UltraSDKCallBack, SDKMessageCallback {

    lateinit var sdkManager: SDKManager
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

    fun initiateSDK(context: Context, subInfo: SubscriptionInfo) {
        SDKMan.initialize(context, subInfo.subscriptionId.toString(), subInfo.number)
        sdkManager = SDKMan.sdk
        sdkManager.setSdkMessageCallback(this)
        sdkManager.invokeSDK(
            "XCH", // correct
            "+919643339706", // VMN (providing soon)
            this
        )
//        sdkManager.listAccountProvider(
//            SDKMan.getAccountRequest().second,
//            SDKMan.getAccountRequest().first,
//            this
//        )
    }

    override fun onResponse(res: USDKResponse?) {
        Log.e("UPI", "Response Success : $res")
        responseLiveData.value = res?.let { ResponseState.Success(it.reqCode, it.response) }
    }

    override fun onFailure(res: USDKResponse?, err: Throwable?) {
        Log.e("UPI", "Response Failure : $res")

        Log.e("UPI", "Failure  Error: ${err?.localizedMessage}")
        errorLiveData.value = res?.message
    }

    override fun nextMsgForProgress(msg: String) {
        Log.d("UPI", "nextMsgForProgress : $msg")
        responseLiveData.value = ResponseState.Success("BINDING", msg)
    }

    override fun toggleDeviceBindingBackGroundFail() {
        Log.e("UPI", "Device BindingBackgroundFail")
    }

}

sealed class ResponseState {
    data class Success(val reqCode: String, val msg: String) : ResponseState()
    data class Error(val msg: String) : ResponseState()
}

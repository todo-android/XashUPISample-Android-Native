package app.xash.upisample

import android.content.Context
import android.telephony.SubscriptionInfo
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.xash.upisample.SDKMan.getSeqNumber
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.ErrorCodes.RequestCodes
import com.icici.ultrasdk.Models.Providers
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.ListAccountProviderReq
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback

class DeviceBindingViewModel : ViewModel(), UltraSDKCallBack, SDKMessageCallback {

    private var sdkManager: SDKManager
    private val errorLiveData = MutableLiveData<String>()
    private val responseLiveData = MutableLiveData<ResponseState.Success>()
    val providers = MutableLiveData<List<Providers>>()

    init {
        sdkManager = SDKMan.sdk
    }

    fun initiateSDK(context: Context, subInfo: SubscriptionInfo) {
        SDKMan.initialize(context, subInfo.subscriptionId.toString(), subInfo.number)
        sdkManager = SDKMan.sdk
        sdkManager.setSdkMessageCallback(this)
        sdkManager.invokeSDK(
            "XCH TRB", // correct
            "+919282123345", // VMN (providing soon)
            this
        )
    }

    fun fetchBankProviders() {
        val reqCode = RequestCodes.LIST_ACCOUNT_PROVIDER.requestCode
        val req = ListAccountProviderReq()
        req.seqNo = getSeqNumber()
        sdkManager.listAccountProvider(req, reqCode, this)
    }

    override fun onResponse(res: USDKResponse) {
        Log.e("UPI", "Response Success : $res")
//        responseLiveData.value = res?.let { ResponseState.Success(it.reqCode, it.response) }
        if (res.reqCode == RequestCodes.LIST_ACCOUNT_PROVIDER.requestCode) {
            providers.postValue(res.mobileAppData.details.providers)
        }
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

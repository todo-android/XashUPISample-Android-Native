package app.xash.upisample.ui.addvpa

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.xash.upisample.SDKMan
import app.xash.upisample.SDKMan.getSeqNumber
import app.xash.upisample.modal.ResponseState
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.ErrorCodes.RequestCodes
import com.icici.ultrasdk.Models.Providers
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.ListAccountProviderReq
import com.icici.ultrasdk.SDKManager
import com.icici.ultrasdk.SDKMessageCallback

class AddVpaViewModel : ViewModel(), UltraSDKCallBack {

    private var sdkManager: SDKManager = SDKMan.sdk
    private val errorLiveData = MutableLiveData<String>()
    val providers = MutableLiveData<List<Providers>>()

    fun fetchBankProviders() {
        val reqCode = RequestCodes.LIST_ACCOUNT_PROVIDER.requestCode
        val req = ListAccountProviderReq()
        req.seqNo = getSeqNumber()
        sdkManager.listAccountProvider(req, reqCode, this)
    }

    override fun onResponse(res: USDKResponse) {
        Log.e("UPI", "Response Success : $res")
        if (res.reqCode == RequestCodes.LIST_ACCOUNT_PROVIDER.requestCode) {
            providers.postValue(res.mobileAppData.details.providers)
        }
    }

    override fun onFailure(res: USDKResponse?, err: Throwable?) {
        Log.e("UPI", "Response Failure : $res")

        Log.e("UPI", "Failure  Error: ${err?.localizedMessage}")
        errorLiveData.value = res?.message
    }


}



package app.xash.upisample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import app.xash.upisample.databinding.ActivityMainBinding
import app.xash.upisample.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var binding: ActivityMainBinding
    var PERMISSIONS = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE
    )
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.containsValue(false)) {
                binding.root.showSnackbar(
                    R.string.permission_denied,
                    Snackbar.LENGTH_SHORT,
                    R.string.ok
                )
            } else {
                initSDK()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        checkPermissionForSms()
    }

    private fun checkPermissionForSms() {
        when {
            hasPermissions(this, *PERMISSIONS) -> {
                initSDK()
            }
            else -> {
                if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {

                    binding.root.showSnackbar(
                        R.string.access_required,
                        Snackbar.LENGTH_INDEFINITE,
                        R.string.ok
                    ) {
                        requestPermissionLauncher.launch(PERMISSIONS)
                    }
                } else {
                    // You can directly ask for the permission.
                    requestPermissionLauncher.launch(PERMISSIONS)

                }
            }
        }
    }

    //firstname.phonenumber.slash@icici
    @SuppressLint("MissingPermission")
    fun initSDK() {
        val subscriptionManager =
            getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0)
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

        if (response.reqCode.equals(RequestCodes.AUTHENTICATION.requestCode)) {
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
//                binding.button2.let {
//                    it.isEnabled = true
//                    it.setOnClickListener {
//                        startActivity(Intent(this, AddVpaActivity::class.java))
//                    }
//                }
            } else if (response.response.equals("1") && response.userProfile.isNullOrEmpty()) {
//                binding.button2.let {
//                    it.isEnabled = true
//                    it.setOnClickListener {
////                        startActivity(Intent(this, MainActivity2::class.java))
//                    }
//                }
            }
        } else if (response.reqCode.equals(RequestCodes.GET_PROFILE_DETAILS.requestCode, true)) {
            if (response.response
                    .equals("0") && response.mobileAppData != null && response.mobileAppData
                    .details != null &&
                response.mobileAppData.details.accounts != null
            ) {
                //calling service to get profile details - gives existing list of accounts and VPAs tagged to them.
                SDKMan.setAccountList(response.mobileAppData.details.accounts as ArrayList<Accounts>)
//                binding.button3.let {
//                    it.isEnabled = true
//                    it.setOnClickListener {
////                        startActivity(Intent(this, PayVPActivity::class.java))
//                    }
//                }
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

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

}
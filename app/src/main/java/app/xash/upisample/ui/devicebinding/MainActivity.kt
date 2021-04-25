package app.xash.upisample.ui.devicebinding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import app.xash.upisample.R
import app.xash.upisample.databinding.ActivityMainBinding
import app.xash.upisample.databinding.SimSelectionDialogBinding
import app.xash.upisample.modal.ResponseState
import app.xash.upisample.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import com.icici.ultrasdk.SDKManager

private var PERMISSIONS = arrayOf(
    Manifest.permission.SEND_SMS,
    Manifest.permission.READ_PHONE_STATE
)

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val simSelectionDialog: AlertDialog by lazy {
        AlertDialog.Builder(this).create()
    }
    private val subscriptionManager: SubscriptionManager by lazy {
        getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }
    private val vm: DeviceBindingViewModel by viewModels()
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
                init()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermissions()
    }

    //firstname.phonenumber.slash@icici
    @SuppressLint("MissingPermission")
    private fun init() {
        if (subscriptionManager.activeSubscriptionInfoCount == 1) {
            binding.bindDeviceButton.isEnabled = false
            vm.initialize(this, getSimInfo(0))
        }
        binding.bindDeviceButton.setOnClickListener {
            showSimDialog()
        }
        vm.result.observe(this, {
            when (it) {
                is ResponseState.Error -> {
                    binding.root.showSnackbar(
                        it.msg,
                        Snackbar.LENGTH_SHORT,
                    )
                }
                is ResponseState.Success -> {

                }
            }
        })
    }

//    override fun onResponse(response: USDKResponse) {
//        if (response.reqCode.equals(RequestCodes.GET_PROFILE_DETAILS.requestCode, true)) {
//            if (response.response
//                    .equals("0") && response.mobileAppData != null && response.mobileAppData
//                    .details != null &&
//                response.mobileAppData.details.accounts != null
//            ) {
//                //calling service to get profile deta ils - gives existing list of accounts and VPAs tagged to them.
//                SDKMan.setAccountList(response.mobileAppData.details.accounts as ArrayList<Accounts>)
//            }
//        }
//    }


    private fun showSimDialog() {
        val dialogView = SimSelectionDialogBinding.inflate(layoutInflater)
        var selectedSimIndex = 0
        with(dialogView) {
            getSimInfo(0).let {
                simSelectionPrimaryCarrierTextView.text =
                    getSimInfo(0).carrierName.toString().capitalize()
                simSelectionPrimaryNumberTextView.text =
                    getSimInfo(0).number.toString()
                simSelectionPrimaryLayout.setOnClickListener {
                    selectedSimIndex = 0
                    simSelectionConfirmBtn.isEnabled = true
                    simSelectionSecondaryImgView.isVisible = false
                    simSelectionPrimaryImgView.isVisible = true
                }
            }

            getSimInfo(1).let {
                simSelectionSecondaryCarrierTextView.text =
                    it.carrierName.toString().capitalize()
                simSelectionSecondaryNumberTextView.text =
                    it.number.toString()
                simSelectionSecondaryLayout.setOnClickListener {
                    selectedSimIndex = 1
                    simSelectionConfirmBtn.isEnabled = true
                    simSelectionPrimaryImgView.isVisible = false
                    simSelectionSecondaryImgView.isVisible = true
                }
            }

            simSelectionConfirmBtn.setOnClickListener {
                vm.initialize(this@MainActivity, getSimInfo(selectedSimIndex))
                simSelectionDialog.dismiss()
            }
            simSelectionCancelBtn.setOnClickListener {
                simSelectionDialog.dismiss()
            }
        }
        simSelectionDialog.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(dialogView.root)
            setCancelable(false)
        }
        simSelectionDialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getSimInfo(index: Int): SubscriptionInfo {
        return subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(index)
    }

    private fun checkPermissions() {
        when {
            hasPermissions(this, *PERMISSIONS) -> {
                init()
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

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

}
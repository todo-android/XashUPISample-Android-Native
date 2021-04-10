package app.xash.upisample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import app.xash.upisample.databinding.ActivityPayVpactivityBinding
import com.icici.ultrasdk.AdaptersAndCallbacks.UltraSDKCallBack
import com.icici.ultrasdk.ErrorCodes.RequestCodes
import com.icici.ultrasdk.Models.Accounts
import com.icici.ultrasdk.Models.USDKResponse
import com.icici.ultrasdk.RequestModels.ListAccounsReq
import com.icici.ultrasdk.RequestModels.ListCustomerAccountReq
import com.icici.ultrasdk.RequestModels.PayToVirtualAccountReq
import com.icici.ultrasdk.RequestModels.ValidateVirtualAddressReq
import com.icici.ultrasdk.SDKManager


class PayVPActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, UltraSDKCallBack {
    lateinit var sdkManager: SDKManager
    private lateinit var binding: ActivityPayVpactivityBinding
    private var isVpaAvailable = false
    private var selectedAccount = Accounts()
    private var customerAccounts: ArrayList<Accounts> = ArrayList()
    private val vpasList: ArrayList<String> = ArrayList()
    var vpa: String? = null
    var accountNamesList: ArrayList<Accounts> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayVpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sdkManager = SDKMan.sdk

        vpasList.add("select")
        vpasList.addAll(SDKMan.getVpaList())
        setSpinnerItems(binding.vpaSpinner, vpasList)
        binding.submitVpa.setOnClickListener {
            val reqCode = RequestCodes.VALIDATE_VPA.requestCode
            val toName = "rohit patekar"
            val req = ValidateVirtualAddressReq()
            req.seqNo = SDKMan.getSeqNumber()
            req.payeeName = toName //to name

            req.virtualAddress = binding.vpaInputLayout.editText?.text.toString() //to vpa
            sdkManager.validateVPA(req, reqCode, this)
        }
        binding.vpaSpinner.onItemSelectedListener = this
    }

    private fun setSpinnerItems(spinner: Spinner, arrayList: ArrayList<String>) {
        val dataAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, arrayList
        )
        when (spinner.id) {
            R.id.vpaSpinner -> {
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = dataAdapter
            }
            else -> {
            }
        }
    }

    override fun onItemSelected(adapterView: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        when (adapterView.id) {
            R.id.vpaSpinner -> {
                vpa = binding.vpaSpinner.selectedItem.toString()
                if (vpa != "select") {
                    val reqCode = RequestCodes.GET_CUSTOMER_ACCOUNTS.requestCode
                    isVpaAvailable = true
                    val req = ListCustomerAccountReq()
                    req.va = vpa
                    req.seqNo = SDKMan.getSeqNumber()
                    sdkManager.getCustomerAccounts(req, reqCode, this)
                }
            }
            else -> {
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onResponse(response: USDKResponse) {
        Log.e("UPI", "FAIL :    ${response} ")

        if (response.reqCode.equals(RequestCodes.GET_CUSTOMER_ACCOUNTS.requestCode) && response.mobileAppData != null
        ) {
            customerAccounts = response.mobileAppData.details.accounts as ArrayList<Accounts>
            val reqCode = RequestCodes.LIST_ACCOUNTS.requestCode
            val payerName = "SDK TESTER"
            val linkType = "MOBILE" // MOBILE/AADHAAR
            val aadhaarConsent = "N" // Y/N
            val listAccounsReq = ListAccounsReq()
            listAccounsReq.aadharrConsent = aadhaarConsent //optional
            listAccounsReq.linkType = linkType //optional
            listAccounsReq.payerName = payerName
            listAccounsReq.accountProvider = "74"
            listAccounsReq.seqNo = SDKMan.getSeqNumber()
            sdkManager.listAccounts(listAccounsReq, reqCode, this)
            if (vpa != "select") {
                selectedAccount = getCustomerAccountByVpa(vpa!!)!!
            }
        } else if (response.reqCode.equals(RequestCodes.LIST_ACCOUNTS.requestCode)) {
            if (response.mobileAppData != null && response.mobileAppData
                    .details != null && response.mobileAppData.details
                    .accounts != null
            ) {
                // set to spinner
                accountNamesList = response.mobileAppData.details.accounts as ArrayList<Accounts>
                var account: Accounts? = null
                if (selectedAccount != null) {
                    account = getListAccountByName(selectedAccount.ifsc)
                }
                if (account != null) {
                    selectedAccount.allowedCredentials = account.allowedCredentials
                }
            }
        } else if (response.reqCode
                .equals(RequestCodes.VALIDATE_VPA.requestCode)
        ) {
            if (response.mobileAppData != null && response.mobileAppData
                    .result != null && response.mobileAppData.result
                    .contains("SUCCESS")
            ) {

                val req = PayToVirtualAccountReq()
                req.amount = binding.amountInputLayout.editText?.text.toString()
                req.payeeVa = binding.vpaInputLayout.editText?.text.toString()
                req.payerVa = vpa
                req.payeeName = "rohit patekar"
                req.payerName = "Gopi"
                req.accountNumber = selectedAccount.account
                req.accountProvider = "74"
                req.ifsc = selectedAccount.ifsc
                req.note = "Testing"
                req.txnType = "payRequest"
                req.preApproved = "M"

                req.allowedCredentialsList = selectedAccount.credsAllowed
                req.seqNo = SDKMan.getSeqNumber()
                req.accountType = selectedAccount.accountType
                req.defaultCredit = "D"
                req.defaultDebit = "D"
                req.useDefaultAcc = "D"
                sdkManager.payToVPA(
                    req,
                    RequestCodes.PAY_TO_VPA.requestCode,
                    this
                )
            }
        }
    }

    override fun onFailure(response: USDKResponse, p1: Throwable) {
        Log.e("UPI", "FAIL :    ${response.message} ", p1)

    }

    private fun getCustomerAccountByVpa(vpa: String): Accounts? {
        for (accounts in customerAccounts) {
            if (accounts.va == vpa) {
                return accounts
            }
        }
        return null
    }

    fun getListAccountByName(name: String): Accounts? {
        for (accounts in accountNamesList) {
            if (accounts.ifsc == name) {
                return accounts
            }
        }
        return null
    }

}
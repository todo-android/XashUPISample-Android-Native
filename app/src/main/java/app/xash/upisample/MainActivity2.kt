package app.xash.upisample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import app.xash.upisample.databinding.ActivityMain2Binding
import app.xash.upisample.vpa.SelectBankForVpaFragment
import com.icici.ultrasdk.Models.Accounts
import com.icici.ultrasdk.Models.Providers
import com.icici.ultrasdk.SDKManager
import java.util.*


class MainActivity2 : AppCompatActivity() {
    lateinit var sdkManager: SDKManager
    private lateinit var binding: ActivityMain2Binding
    private var isValidVPA = false
    private var providersList: ArrayList<Providers> = ArrayList()
    private var accountsList: ArrayList<Accounts> = ArrayList()

    private var selectedProvider: Providers = Providers()
    private var selectedAccount = Accounts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SelectBankForVpaFragment>(R.id.fragment_container_view)
            }
        }

        sdkManager = SDKMan.sdk

//        binding.button.setOnClickListener {
//            reqCode = RequestCodes.CHECK_VPA_AVAILABILITY.requestCode
//            val vpa: String = binding.textInputLayout.editText?.text.toString()
//            if (vpa != "") {
//                val checkAvailabilityReq = CheckAvailabilityReq()
//                checkAvailabilityReq.va = vpa
//                checkAvailabilityReq.seqNo = getSeqNumber()
//                sdkManager.checkVPAAvailability(checkAvailabilityReq, reqCode, this)
//            }
//        }
//        binding.bankSpinner.onItemSelectedListener = this
//        binding.accountSpinner.onItemSelectedListener = this
//
//        binding.submitVpa.setOnClickListener {
//            checkNotNull(selectedAccount)
//            createVPA()
//        }


    }

//    private fun createVPA() {
//
//
//        reqCode = RequestCodes.ADD_ACCOUNT.requestCode
//        val req = AddAccountReq()
//        req.selectedAccount = selectedAccount
//        req.accountProvider = selectedProvider.id.toString() + ""
//        req.payerVa = binding.textInputLayout.editText?.text.toString()
//        req.cardDigits = binding.cardInputLayout.editText?.text.toString()
//        req.expiryDate = binding.dateInputLayout.editText?.text.toString()
//        req.accountType = selectedAccount.accountType ?: "SAVINGS"
//        req.defaultCredit = "D"
//        req.defaultDebit = "D"
//        if (req.selectedAccount != null) {
//            if (req.selectedAccount.mbeba == "Y") {
//                //AddAccount Process handle
//                val storeAccountDetailsReq = StoreAccountDetailsReq()
//                storeAccountDetailsReq.accountNumber =
//                    req.selectedAccount.accRefNumber //actual no.
//                storeAccountDetailsReq.accountProvider = req.accountProvider
//                storeAccountDetailsReq.ifsc = req.selectedAccount.ifsc
//                storeAccountDetailsReq.mmid = req.selectedAccount.mmid
//                storeAccountDetailsReq.name = req.selectedAccount.name
//                storeAccountDetailsReq.va = req.payerVa
//                storeAccountDetailsReq.accountType = req.selectedAccount.accountType
//                storeAccountDetailsReq.accountType = ("SAVINGS")
//                storeAccountDetailsReq.defaultDebit = "D"
//                storeAccountDetailsReq.defaultCredit = "D"
//                storeAccountDetailsReq.seqNo = getSeqNumber()
//                sdkManager.storeAccountDetails(
//                    storeAccountDetailsReq,
//                    reqCode,
//                    this,
//                    req
//                )
//            } else {
//                //Generate OTP Process handle
//                val generateOTPReq = GenerateOTPReq()
//                generateOTPReq.accountNumber = req.selectedAccount.accRefNumber //actual no.
//                generateOTPReq.accountProvider = req.accountProvider
//                generateOTPReq.ifsc = req.selectedAccount.ifsc
//                generateOTPReq.payerName = req.selectedAccount.name
//                generateOTPReq.payerVa = req.payerVa
//                generateOTPReq.seqNo = getSeqNumber()
//                sdkManager.generateOTPForVpaCreation(
//                    generateOTPReq,
//                    reqCode,
//                    req,
//                    this
//                )
//            }
//        } else {
//            val response = USDKResponse()
//            response.reqCode = reqCode
//            response.response = ErrorCodes.U105.errorCode
//            this.onFailure(response, null)
//        }
//
//    }
//
//    private fun getSeqNumber(): String {
//        return "ICI" + java.lang.String.valueOf(UUID.randomUUID())
//            .replace("[\\s\\-()]".toRegex(), "")
//    }
//
//
//    override fun onResponse(response: USDKResponse) {
//        if (response.reqCode.equals(RequestCodes.CHECK_VPA_AVAILABILITY.requestCode)) {
//            if (response.response.equals("0")) {
//                isValidVPA = true
//                if (providersList.size == 0) {
//                    //need to call list account provider and get list
//                    val reqCode = RequestCodes.LIST_ACCOUNT_PROVIDER.requestCode
//                    val req = ListAccountProviderReq()
//                    req.seqNo = getSeqNumber()
//                    sdkManager.listAccountProvider(req, reqCode, this)
//                }
//            } else {
//                binding.textInputLayout.editText?.setText("")
//                Toast.makeText(this, "VPA is in use,Please enter other vpa", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        } else if (response.reqCode
//                .equals(RequestCodes.LIST_ACCOUNT_PROVIDER.requestCode)
//        ) {
//            val providerNameList: ArrayList<String> = ArrayList()
//
//            providersList = response.mobileAppData.details.providers as ArrayList<Providers>
//            providerNameList.add("select")
//            providersList.forEach {
//                providerNameList.add(it.accountProvider)
//            }
//            setSpinnerItems(binding.bankSpinner, providerNameList)
//
//        } else if (response.reqCode.equals(RequestCodes.LIST_ACCOUNTS.requestCode)) {
//            if (response.mobileAppData != null && response.mobileAppData
//                    .details != null && response.mobileAppData.details
//                    .accounts != null
//            ) {
//                val accountNamesList: ArrayList<String> = ArrayList()
//                accountNamesList.add("select")
//                accountsList =
//                    response.mobileAppData.details.accounts as ArrayList<Accounts>
//
//                accountsList.forEach {
//                    accountNamesList.add(it.account)
//                }
//                setSpinnerItems(binding.accountSpinner, accountNamesList)
//            }
//        } else if (response.reqCode.equals(RequestCodes.BALANCE_ENQUIRY.requestCode)) {
//            Toast.makeText(
//                this,
//                " Transaction is: " + response.mobileAppData.result,
//                Toast.LENGTH_LONG
//            ).show()
//        } else {
//            Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
//        }
//    }
//
//    private fun setSpinnerItems(spinner: Spinner, arrayList: ArrayList<String>) {
//        val dataAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item, arrayList
//        )
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = dataAdapter
//    }
//
//    override fun onFailure(response: USDKResponse, p1: Throwable?) {
//        Toast.makeText(this, response.response, Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onItemSelected(adapterView: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
//        when (adapterView.id) {
//            R.id.bankSpinner -> {
//                val providerName: String = binding.bankSpinner.selectedItem.toString()
//                if (providerName != "select") {
//                    reqCode = RequestCodes.LIST_ACCOUNTS.requestCode
//                    selectedProvider = getProviderByName(providerName)!!
//                    val payerName = "SDK TESTER"
//                    val linkType = "MOBILE" // MOBILE/AADHAAR
//                    val aadhaarConsent = "N" // Y/N
//                    val listAccounsReq = ListAccounsReq()
//                    listAccounsReq.aadharrConsent = aadhaarConsent //optional
//                    listAccounsReq.linkType = linkType //optional
//                    listAccounsReq.payerName = payerName
//                    listAccounsReq.accountProvider = selectedProvider.id.toString() + ""
//                    listAccounsReq.seqNo = getSeqNumber()
//                    sdkManager.listAccounts(listAccounsReq, reqCode, this)
//                }
//            }
//            R.id.accountSpinner -> {
//                val account: String = binding.accountSpinner.selectedItem.toString()
//                selectedAccount = getAccountByName(account) ?: Accounts()
//            }
//            else -> {
//            }
//        }
//    }
//
//    override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("Not yet implemented")
//    }
//
//    private fun getProviderByName(name: String?): Providers? {
//        for (providers in providersList) {
//            if (providers.accountProvider.equals(name, ignoreCase = true)) {
//                return providers
//            }
//        }
//        return null
//    }
//
//    private fun getAccountByName(name: String): Accounts? {
//        for (accounts in accountsList) {
//            if (accounts.account.equals(name, ignoreCase = true)) {
//                return accounts
//            }
//        }
//        return null
//    }

}
/**
ACI Worldwide'
AIRTEL PAYMENTS BANK
AXIS
AXIS Google'
Aditya Birla Idea Payments Bank
Apna Sahakari Bank Ltd
Assam Gramin VIkash Bank
Axis Phonepe'
Axis bank_JusPay'
Axis bank_JusPay
BSGIT'
Bandhan'
Bank of America N.A.'
Bank of Baroda
Bank of India New'
Bank of Maharashtra
Bank1'
BankofIndia
Bharat Co-op Bank'
CANARA'
CANARA New
CERT1'
CERT2'
CERT3'
CERT4'
CERT5'
CITY UNION BANK
CSBK'
Central Bank of India
Citi bank Retail'
DBS BANK LTD
DCB'
DCB Bank'
Dena Bank
Deutsche Bank'
Deutsche Bank AG
Dhanlaxmi Bank
Dombivli Nagari bank
ESAF SMALL FINANCE BANK LIMITED
Equitas Small Finance Bank
FEDE'
FEDEPSP'
Fincare Bank'
Fino Payments Bank
GPPB
HCE Secure IT Serv'
HDFC
HDFC razorpay
HSBC Bank'
HSBC Bank_test'
Happay PPI Integrations'
Himachal Pradesh Gramin Bank
ICICI'"
ICICI Bank'
ICICI pockets'
ICICIPSP'
IDBI
IDFC First Bank'
India Post Payments Bank Limited
Indian Bank
IndianOverseasBank
Indusind Bank
Jammu and Kashmir ltd
Jio Payments Bank 1'
Jio payments bank
JusPay'
KARNATAKABANK'
KVB
Kalyan Janata Sahakari bank
Karnataka Vikas Grameena Bank
Kashi Gomti Samyut Gramin Bank
Kotak mahindra Bank ltd
LAKSHMI VILAS BANK
Livquik'
MAHARASHTRA GRAMIN BANK
MANIPUR RURAL BANK
MINDGATE'
MYCARDS'
MYPSP
MYPSP2
Madhya Bihar Gramin Bank
Mobiwik'
Montran India'
MyCM
Mybank
Mybene
PAYTM PAYMENTS BANK
PMCB
PNB migration
PSB
Payu Kotak'
Punjab Gramin Bank
RBL
Rajkot Nagarik
SBI
SBI New'
SBI whatsapp'
SIL'
Shivalik Bank'
South Indian Bank'
Standard Chartered
State bank of India fresh'
Suryoday Small Finance Bank
Syndicate'
Syndicate Bank
TECH TEAM'"
TEST
THE COSMOS CO-OPERATIVE BANK LTD
Tamilnad Mercantile
Thrissur  District Cooperative Bank Ltd'
Transcorp International'
Tripura Gramin Bank
UCO'
Ujjivan Small Finance Bank Limited
Union Bank of India
United Bank of India'
Utkarsh Small Finance Bank'
Vijaya Bank
Walnut
Yes Bank
Yes Juspay''allahabad bank
citi bank
wallnut PNB**/
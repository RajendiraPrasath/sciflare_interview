package com.example.sciflare_interview.view.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sciflare_interview.R
import com.example.sciflare_interview.databinding.SenderScreenBinding
import com.example.sciflare_interview.model.utills.Utils
import com.example.sciflare_interview.view.adapter.MessageAdapter
import com.example.sciflare_interview.viewmodel.SenderViewModel


class SenderActivity: AppCompatActivity() {

    private val permissionSendSms = 123
    private lateinit var senderScreenBinding: SenderScreenBinding
    private lateinit var viewModel: SenderViewModel
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var receiverIcon: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowTitleEnabled(true)
        senderScreenBinding = SenderScreenBinding.inflate(layoutInflater)
        setContentView(senderScreenBinding.root)
        viewModel = ViewModelProvider(this)[SenderViewModel::class.java]
        setMessageAdapter()
        Utils.progressAlertDialog = Utils.createProgressDialog(this)

        /* Get Latest 5 sender message */
        viewModel.getLastFiveMessages(this){
            Utils.hideProgressDialog()
            if (it.size > 0 && this::receiverIcon.isInitialized) {
                receiverIcon.isVisible = true
            }
            messageAdapter.messages = it.reversed().toMutableList()
        }

        /* Send Button Click Action*/
        senderScreenBinding.button.setOnClickListener {
            if (senderScreenBinding.editText.text.toString().isNotEmpty()) {
                requestSmsPermission()
            } else {
                Toast.makeText(this,applicationContext.resources.getString(R.string.empty_error_message),Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* Set Message Adapter */
    private fun setMessageAdapter(){
        messageAdapter = MessageAdapter(this)
        senderScreenBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        senderScreenBinding.recyclerView.adapter = messageAdapter
    }

    /* Ask Runtime Permissions for end User */
    private fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_NUMBERS),
                permissionSendSms
            )
        } else {
            getDevicePhoneNumber()
        }
    }

    /* Get Runtime Permission Result */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionSendSms -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDevicePhoneNumber()
                } else {
                    Toast.makeText(this,applicationContext.resources.getString(R.string.permission_denied_message),Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    /* Get current device mobile number, Encrypt sender message & SMS send to the current device mobile number  */
    private fun getDevicePhoneNumber(){
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        Utils.displayProgressDialog()
        viewModel.messageEncrypt(senderScreenBinding.editText.text.toString(),applicationContext,telephonyManager.line1Number)
    }

    /* Open Receiver Screen */
    private fun callReceiverActivity(){
        startActivity(Intent(applicationContext, ReceiverActivity::class.java))
    }

    /* Create Receiver Menu Icon */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    /* Handled Menu icon click */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.receiver -> {
                callReceiverActivity()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /* Disable the menu icon upon the initial opening of the app */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            receiverIcon = menu.findItem(R.id.receiver)
            receiverIcon.isVisible = false
        }

        return super.onPrepareOptionsMenu(menu)
    }
}
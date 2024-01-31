package com.example.sciflare_interview.viewmodel

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sciflare_interview.R
import com.example.sciflare_interview.model.room.Message
import com.example.sciflare_interview.model.room.MessageDataBase
import com.example.sciflare_interview.model.utills.Utils
import com.example.sciflare_interview.model.utills.Utils.Companion.getGradleProperty
import kotlinx.coroutines.launch

typealias GetLastFiveMessageUpdate = (messageList: MutableList<Message>) -> Unit

class SenderViewModel: ViewModel() {

    /* Encrypt to Sender Message */
    fun messageEncrypt(senderMessage: String,context: Context, senderNumber: String){
        val encryptMessage = Utils.encryptAES(senderMessage, getGradleProperty(context, context.resources.getString(R.string.key)),context)
        encryptMessage?.let {
            viewModelScope.launch {
                sendSms(encryptMessage,context,senderNumber)
            }
        }
    }

    /* Send SMS to Current Device Mobile Number */
    private fun sendSms(message: String,context: Context,senderNumber: String){
        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }
        smsManager.sendTextMessage(senderNumber, null, message, null, null)
    }

    /* Get Last 5 messages from locale DB */
    fun getLastFiveMessages(context: Context, getLastFiveMessageUpdate: GetLastFiveMessageUpdate){
        val messageDao by lazy { MessageDataBase.getDatabase(context).messageDao() }
        viewModelScope.launch {
            messageDao.getLastFiveMessages().collect {
                getLastFiveMessageUpdate.invoke(it.toMutableList())
            }
        }
    }
}
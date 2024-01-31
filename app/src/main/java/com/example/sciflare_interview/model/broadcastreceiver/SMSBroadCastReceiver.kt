package com.example.sciflare_interview.model.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.example.sciflare_interview.model.room.Message
import com.example.sciflare_interview.model.room.MessageDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SMSBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action.equals( SMS_RECEIVED)) {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<Any>?
                val messages: Array<SmsMessage?> = arrayOfNulls<SmsMessage>(pdus!!.size)
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                }
                if (messages.size > -1) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val message by lazy { context?.applicationContext?.let {
                            MessageDataBase.getDatabase(
                                it
                            ).messageDao()
                        } }
                        val messageText = messages[0]?.messageBody?.let { Message(message = it) }
                        if (messageText != null) {
                            message?.addMessage(messageText)
                        }
                    }

                }
            }
        }
    }

    companion object {
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
        private const val TAG = "SMSBroadcastReceiver"
    }
}
package com.example.sciflare_interview.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sciflare_interview.model.room.Message
import com.example.sciflare_interview.model.room.MessageDataBase
import kotlinx.coroutines.launch

typealias OnMessageUpdate = (messageList: MutableList<Message>) -> Unit

class ReceiverViewModel: ViewModel() {
    fun getAllMessages(context: Context, onMessageUpdate: OnMessageUpdate){
        val messageDao by lazy { MessageDataBase.getDatabase(context).messageDao() }
        viewModelScope.launch {
            messageDao.getAllMessages().collect {
                onMessageUpdate.invoke(it.toMutableList())
            }
        }
    }
}
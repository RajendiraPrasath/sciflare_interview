package com.example.sciflare_interview.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sciflare_interview.R
import com.example.sciflare_interview.model.room.Message
import com.example.sciflare_interview.model.utills.Utils

class MessageAdapter(val context: Context) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    var messages: MutableList<Message?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: AppCompatTextView = itemView.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.message_adapter, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        message?.message?.let {
            holder.messageTextView.text = Utils.decryptAES(message.message,
                Utils.getGradleProperty(context, context.resources.getString(R.string.key)), context)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}
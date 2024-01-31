package com.example.sciflare_interview.view.screens

import android.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sciflare_interview.view.adapter.MessageAdapter
import com.example.sciflare_interview.databinding.ReceiverScreenBinding
import com.example.sciflare_interview.model.room.MessageDataBase
import com.example.sciflare_interview.viewmodel.ReceiverViewModel

class ReceiverActivity: AppCompatActivity() {
    private lateinit var receiverScreenBinding: ReceiverScreenBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var viewModel: ReceiverViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        receiverScreenBinding = ReceiverScreenBinding.inflate(layoutInflater)
        setContentView(receiverScreenBinding.root)

        viewModel = ViewModelProvider(this)[ReceiverViewModel::class.java]
        setMessageAdapter()

        viewModel.getAllMessages(this) {
            messageAdapter.messages = it.toMutableList()
        }
    }
    private fun setMessageAdapter(){
        messageAdapter = MessageAdapter(this)
        receiverScreenBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        receiverScreenBinding.recyclerView.adapter = messageAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
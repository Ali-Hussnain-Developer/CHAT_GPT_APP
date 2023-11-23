package com.example.chatgpt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgpt.R
import com.example.chatgpt.model.Message

class MessageAdapter(private val mmsg: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    companion object {
        private const val userviewtype = 0
        private const val botviewtype = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = if (viewType == userviewtype) {
            inflater.inflate(R.layout.item_message_user, parent, false)
        } else {
            inflater.inflate(R.layout.item_message_bot, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = mmsg[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return mmsg.size
    }

    override fun getItemViewType(position: Int): Int {
        val msg = mmsg[position]
        return if (msg.isSentByUser) userviewtype else botviewtype
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(message: Message) {
            if (message.isSentByUser) {
                val mtext: TextView = itemView.findViewById(R.id.text_message_user)
                mtext.text = message.mText
            } else {
                val mtext: TextView = itemView.findViewById(R.id.text_message_bot)
                mtext.text = message.mText
            }
        }
    }
}

package com.example.dynamicmessenger.userHome.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dynamicmessenger.R
import com.example.dynamicmessenger.activitys.ChatRoomActivity
import com.example.dynamicmessenger.common.SharedConfigs
import com.example.dynamicmessenger.network.DownloadImageTask
import com.example.dynamicmessenger.network.authorization.LoadAvatarApi
import com.example.dynamicmessenger.network.authorization.models.Chat
import com.example.dynamicmessenger.userChatRoom.fragments.ChatRoomFragment
import com.example.dynamicmessenger.userDataController.SharedPreferencesManager
import com.example.dynamicmessenger.userDataController.database.DiskCache
import com.example.dynamicmessenger.userHome.fragments.UpdateUserInformationFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class UserChatsAdapter(val context: Context, job: Job, val activity: Activity) : RecyclerView.Adapter<UserChatsAdapter.UserChatsViewHolder>(){
    var data = mutableListOf<Chat>()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)
    private val diskLruCache = DiskCache.getInstance(context)

    fun setAdapterData(newList: List<Chat>) {
        data.clear()
        data.addAll(newList)
    }

    fun setAdapterDataNotify(newList: List<Chat>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }

    fun submitList(newList: List<Chat>) {
        val userChatDiffUtilCallback = UserChatDiffUtilCallback(data, newList)
        val authorDiffResult = DiffUtil.calculateDiff(userChatDiffUtilCallback)
        authorDiffResult.dispatchUpdatesTo(this)
        data.clear()
        data.addAll(newList)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chats_item_view, parent, false)
        return UserChatsViewHolder(view, context)
    }


    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: UserChatsViewHolder, position: Int) {
        val item = data[position]
        val timeInHours: String
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date: Date = format.parse(item.message?.createdAt)!!
            val currentDate: Date = Calendar.getInstance().time
            if ((currentDate.day == date.day) && (currentDate.month == date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("HH:mm")
                timeInHours = newFormat.format(date)
                holder.messageTime.text = timeInHours
            } else if ((currentDate.day != date.day) || (currentDate.month != date.month) && (currentDate.year == date.year)) {
                val newFormat = SimpleDateFormat("MMMM-dd")
                timeInHours = newFormat.format(date)
                holder.messageTime.text = timeInHours
            } else {
                holder.messageTime.setText(R.string.long_time_ago)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.name.text = item.name ?: item.username
        holder.chat = item
        holder.lastname.text = item.lastname
        holder.lastMessage.text = item.message?.text
        if (item.recipientAvatarURL != null) {
//            DownloadImageTask(holder.chatUserImageView).execute(item.recipientAvatarURL)
            getAvatar(holder.chatUserImageView, item.recipientAvatarURL)
        } else  {
            holder.chatUserImageView.setImageResource(R.drawable.ic_user_image)
        }
    }

    private fun getAvatar(imageView: ImageView, recipientAvatarURL: String?) {
        coroutineScope.launch {
            if (recipientAvatarURL != null) {
                try {
                    if (diskLruCache.get(recipientAvatarURL) != null) {
                        imageView.setImageBitmap(diskLruCache.get(recipientAvatarURL)!!)
                    } else {
                        val response = LoadAvatarApi.retrofitService.loadAvatarResponseAsync(recipientAvatarURL)
                        if (response.isSuccessful) {
                            val inputStream = response.body()!!.byteStream()
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            diskLruCache.put(recipientAvatarURL, bitmap)
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    Log.i("+++exception", e.toString())
                }
            }
        }
    }

    @SuppressLint("ResourceType")
    inner class UserChatsViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView){
        var chat: Chat? = null
        val name: TextView = itemView.findViewById(R.id.chatsUserName)
        val lastname: TextView = itemView.findViewById(R.id.chatsUserLastname)
        val lastMessage: TextView = itemView.findViewById(R.id.chatsLastMessage)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val chatUserImageView: ImageView = itemView.findViewById(R.id.chatUserImageView)
        init {
            itemView.setOnClickListener {
                (context as AppCompatActivity?)!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer , ChatRoomFragment())
                    .addToBackStack(null)
                    .commit()
                SharedPreferencesManager.setReceiverID(context, chat!!.id)
                SharedPreferencesManager.setReceiverAvatarUrl(context, chat!!.recipientAvatarURL)
            }
        }
    }
}

class UserChatDiffUtilCallback(private val oldList: List<Chat>, private val newList: List<Chat>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].message?.createdAt == newList[newItemPosition].message?.createdAt
    }

}

package com.example.myplayer.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myplayer.Models.UserData
import com.example.myplayer.R
import com.example.myplayer.databinding.ItemsBinding
import com.example.myplayer.ui.SelectFragment

class UserAdapter(val context: Context, val list: ArrayList<UserData>,val OnItemClick: (UserData,postion:Int) -> Unit
) : RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(val itemsBinding: ItemsBinding) : RecyclerView.ViewHolder(itemsBinding.root) {
        fun OnBind(data: UserData,position: Int) {
            itemsBinding.apply {
                tvName.text = data.aftorName
                tvMusic.text = data.musicName

                Glide.with(context)
                    .load(list[position].artUri)
                    .apply (RequestOptions().placeholder(R.drawable.imgssss).centerCrop())
                    .into(imageView)

                btnCard.setOnClickListener {
                    OnItemClick.invoke(data,position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.OnBind(list[position],position)

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
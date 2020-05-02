package com.fahmisbas.randomsnap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(private val userList : ArrayList<User>, private val onItemClickListener: OnItemClick) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    companion object {
        var onItemClick: OnItemClick? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_userlist,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        val user = userList[position]

        holder.tvUserName.text = user.userName

        Glide.with(holder.itemView.context)
            .load(user.imageurl)
            .into(holder.imgProfilePic)

        onItemClick = onItemClickListener
        holder.itemView.setOnClickListener(View.OnClickListener {
            onItemClick?.onItemClick(position)
        })

    }

    class ViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        var tvUserName: TextView = view.findViewById(R.id.tv_username)
        var tvDesc : TextView = view.findViewById(R.id.tv_desc)
        var imgProfilePic : CircleImageView = view.findViewById(R.id.img_profile_picture)
    }

    open interface OnItemClick{
        fun onItemClick(position : Int)
    }


}
package com.example.fotografpaylas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

class HaberRecyclerAdapter(val postList:ArrayList<Post>):RecyclerView.Adapter<HaberRecyclerAdapter.PostHolder>() {
    class PostHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)

    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.itemView.recycler_row_emaili.text=postList[position].kullaniciemail
        holder.itemView.recycler_row_yorumu.text=postList[position].kullaniciyorum
        Picasso.get().load(postList[position].gorselurl).into(holder.itemView.recycler_row_imageView)
        holder.itemView.recycler_row_kullaniciadi.text=postList[position].kullaniciadi

    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
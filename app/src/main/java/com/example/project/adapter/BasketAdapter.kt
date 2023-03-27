package com.example.project.adapter

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project.activity.Home
import com.example.project.databinding.BasketDesignBinding
import com.example.project.databinding.ViewProductBinding
import com.example.project.fragment.DetailsFragment
import com.example.project.model.Product
import com.example.project.model.isBuyed
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class BasketAdapter(var activity: Activity, var data: ArrayList<isBuyed>) :
    RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: BasketDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            BasketDesignBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load(data[position].img).into(holder.binding.imgProduct)
        holder.binding.tvCopies1.setText(data[position].numOfCopy.toString())
        holder.binding.tvTitle.setText(data[position].name)
        holder.binding.tvCost1.setText(data[position].price.toString())
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
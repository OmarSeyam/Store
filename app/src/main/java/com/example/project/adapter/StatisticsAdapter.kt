package com.example.project.adapter

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project.activity.Home
import com.example.project.databinding.BasketDesignBinding
import com.example.project.databinding.StatisticsDesignBinding
import com.example.project.databinding.ViewProductBinding
import com.example.project.fragment.DetailsFragment
import com.example.project.model.Product
import com.example.project.model.isBuyed
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class StatisticsAdapter(var activity: Activity, var data: ArrayList<Product>) :
    RecyclerView.Adapter<StatisticsAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: StatisticsDesignBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            StatisticsDesignBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load(data[position].img).into(holder.binding.imgProduct)
        holder.binding.tvCopies1.setText(data[position].numOfCopies.toString())
        holder.binding.tvTitle.setText(data[position].name)
        var profit= data[position].price!! *data[position].numOfCopies.toString().toDouble()
        holder.binding.tvProfit1.setText(profit.toString())
        var rate:Float
        if(data[position].numRate!=0) {
            rate = data[position].sumRate!! /data[position].numRate.toString().toFloat()
        }else{
            rate =0f
        }
        holder.binding.tvRate1.setText(rate.toString())
        holder.binding.tvSold1.setText(data[position].numOfCopySold.toString())
        holder.binding.tvNumRate1.setText(data[position].numRate.toString())
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
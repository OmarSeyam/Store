package com.example.project.adapter

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project.activity.Home
import com.example.project.databinding.ViewProductBinding
import com.example.project.fragment.DetailsFragment
import com.example.project.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class productAdapter(var activity: Activity, var data: ArrayList<Product>) :
    RecyclerView.Adapter<productAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: ViewProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ViewProductBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        db.collection("category").document(data[position].category.toString())
            .get()
            .addOnSuccessListener {
                var rate:Float
                if(data[position].numRate!=0) {
                     rate = data[position].sumRate!! /data[position].numRate.toString().toFloat()
                }else{
                     rate =0f
                }
                Picasso.get().load(data[position].img).into(holder.binding.imgProduct)
                holder.binding.tvCatagory.setText(it.getString("name"))
                holder.binding.tvTitle.setText(data[position].name)
                holder.binding.ratingBar2.rating=rate
            }
        holder.binding.cardViewDetails.setOnClickListener {
            var d=(activity as Home)
            d.makeCurrentFragment(DetailsFragment())
            val sharedP=d.getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
            val edit=sharedP!!.edit()
            edit.putString("idProduct",data[position].id)
            edit.apply()
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}
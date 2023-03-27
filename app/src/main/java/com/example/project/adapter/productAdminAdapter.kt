package com.example.project.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project.activity.AdminHome
import com.example.project.activity.Home
import com.example.project.databinding.ViewProductAdminBinding
import com.example.project.databinding.ViewProductBinding
import com.example.project.fragment.DetailsAdminFragment
import com.example.project.fragment.DetailsFragment
import com.example.project.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class productAdminAdapter(var activity: Activity, var data: ArrayList<Product>) :
    RecyclerView.Adapter<productAdminAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: ViewProductAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ViewProductAdminBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        Picasso.get().load(data[position].img).into(holder.binding.imgProduct)
        db.collection("category").document(data[position].category.toString())
            .get()
            .addOnSuccessListener {
                holder.binding.tvCatagory.setText(it.getString("name"))
            }
            .addOnFailureListener {
                holder.binding.tvCatagory.setText("category not found")
            }
        var rate:Float
        if(data[position].numRate!=0) {
            rate = data[position].sumRate!! /data[position].numRate.toString().toFloat()
        }else{
            rate =0f
        }
        holder.binding.ratingBar2.rating=rate
        holder.binding.tvTitle.setText(data[position].name)
        holder.binding.btnDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Delete")
            dialog.setMessage("do you want delete this book!")
            dialog.setPositiveButton("yes") { _, _ ->
                db.collection("product").document(data[position].id)
                    .delete()
                    .addOnSuccessListener {
                        data.removeAt(position)
                        notifyDataSetChanged()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Failed delete", Toast.LENGTH_SHORT).show()

                    }
            }
            dialog.setNegativeButton("No") { dis, _ ->
                dis.dismiss()
            }
            dialog.create().show()
        }
        holder.binding.cardViewDetails.setOnClickListener {
            var d=(activity as AdminHome)
            d.makeCurrentFragment(DetailsAdminFragment())
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
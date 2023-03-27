package com.example.project.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project.activity.AdminHome
import com.example.project.activity.Home
import com.example.project.databinding.ViewCategoryAdminBinding
import com.example.project.fragment.DetailsFragment
import com.example.project.fragment.EditCategoryFragment
import com.example.project.model.Category
import com.google.firebase.firestore.FirebaseFirestore


class CategoryAdminAdapter(var activity: Activity, var data: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdminAdapter.MyViewHolder>() {

    class MyViewHolder(var binding: ViewCategoryAdminBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ViewCategoryAdminBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val db = FirebaseFirestore.getInstance()
        holder.binding.tvCatagory.setText(data[position].name)
        holder.binding.btnDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("Delete")
            dialog.setMessage("do you want delete this book!")
            dialog.setPositiveButton("yes") { _, _ ->
                db.collection("category").document(data[position].id)
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
            d.makeCurrentFragment(EditCategoryFragment())
            val sharedP=d.getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
            val edit=sharedP!!.edit()
            edit.putString("idCategory",data[position].id)
            edit.apply()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
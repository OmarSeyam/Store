package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.databinding.FragmentDetailsAdminBinding
import com.example.project.databinding.FragmentDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailsAdminFragment : Fragment() {

    private var _binding: FragmentDetailsAdminBinding? = null
    private val binding get() = _binding!!
    lateinit var d:Activity
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d=(activity as AdminHome)
        getProduct()
        binding.btnUpdate.setOnClickListener {
            (d as AdminHome).makeCurrentFragment(EditProductFragment())
        }
    }

    fun getProduct() {
        showDialog()
        val sharedP = d.getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        val db = FirebaseFirestore.getInstance()
        db.collection("product").document(sharedP.getString("idProduct","x").toString())
            .get()
            .addOnSuccessListener {
                var name = it.getString("name")
                var image = it.getString("img")
                var detail = it.getString("detail")
                var category = it.getString("category")
                var price = it.getDouble("price")
                var sold = it.get("numOfCopySold")
                var quantity = it.get("numOfCopies")
                var sumRate = it.get("sumRate")
                var numRate = it.get("numRate")

                binding.tvTitle.setText(name)
                binding.eddetails.setText(detail)
                binding.edSales.setText(sold.toString())
                Picasso.get().load(image).into(binding.imageProduct)
                binding.edPrice.setText(price.toString())
                binding.edQty.setText(quantity.toString())
                binding.ednumRate.setText(numRate.toString())
                db.collection("category").document(category.toString())
                    .get()
                    .addOnSuccessListener {
                        binding.edCategory.setText(it.getString("name"))
                    }
                    .addOnFailureListener {
                        binding.edCategory.setText("Category Not Found")
                    }
                hideDialog()

            }
            .addOnFailureListener {
                hideDialog()
                Toast.makeText(d,it.message,Toast.LENGTH_SHORT).show()
            }

            }
    private fun showDialog() {
        progressDialog = ProgressDialog(d)
        progressDialog!!.setMessage("Wait ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}
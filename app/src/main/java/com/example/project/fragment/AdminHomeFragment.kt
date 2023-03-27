package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.activity.AdminHome
import com.example.project.adapter.productAdminAdapter
import com.example.project.databinding.FragmentAdminHomeBinding
import com.example.project.databinding.ViewCategoryAdminBinding
import com.example.project.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class AdminHomeFragment : Fragment() {
    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    lateinit var data:ArrayList<Product>
    private var progressDialog: ProgressDialog? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        d = (activity as AdminHome)
        data= ArrayList<Product>()
        getAllProduct()
        binding.category.setOnClickListener {
            (activity as AdminHome).makeCurrentFragment(CategoryFragment())
        }
    }

    fun getAllProduct() {
        showDialog()
        db.collection("product")
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (doc in it) {
                        var product = Product(
                            doc.id,
                            doc.getString("name"),
                            doc.getString("img"),
                            doc.getString("detail"),
                            doc.getString("category"),
                            doc.getDouble("price"),
                            doc.get("numOfCopySold"),
                            doc.get("numOfCopies"),
                            doc.get("sumRate").toString().toFloat() ,
                            doc.get("numRate").toString().toInt(),
                        )
                        data.add(product)
                    }
                    var productAdapter = productAdminAdapter(d, data)
                    binding.rv.layoutManager = LinearLayoutManager(d)
                    binding.rv.adapter = productAdapter
                }
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d,it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
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
package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.adapter.CategoryAdminAdapter
import com.example.project.adapter.productAdminAdapter
import com.example.project.databinding.FragmentCategoryBinding
import com.example.project.databinding.FragmentDetailsAdminBinding
import com.example.project.model.Category
import com.example.project.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    lateinit var data:ArrayList<Category>
    private var progressDialog: ProgressDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            db = FirebaseFirestore.getInstance()
            d = (activity as AdminHome)
            data= ArrayList<Category>()
            getAllCategory()
        }

        fun getAllCategory() {
            showDialog()
            db.collection("category")
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        for (doc in it) {
                            var category = Category(
                                doc.id,
                                doc.getString("name")!!
                            )
                            data.add(category)
                        }
                        var categoryAdapter = CategoryAdminAdapter(d, data)
                        binding.rv.layoutManager = LinearLayoutManager(d)
                        binding.rv.adapter = categoryAdapter
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
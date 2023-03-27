package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.databinding.FragmentDetailsBinding
import com.example.project.databinding.FragmentEditCategoryBinding
import com.example.project.model.Category
import com.google.firebase.firestore.FirebaseFirestore

class EditCategoryFragment : Fragment() {
    private var _binding: FragmentEditCategoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    lateinit var sharedP:SharedPreferences
    lateinit var db:FirebaseFirestore
    lateinit var d:Activity
    lateinit var name:String
    private var progressDialog: ProgressDialog? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d=(activity as AdminHome)
        db=FirebaseFirestore.getInstance()
        sharedP=d.getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        getCategory()
        binding.btnUpdate.setOnClickListener {
            setCategory()
        }


    }

    fun getCategory(){
        showDialog()
        var id =sharedP.getString("idCategory","x")
        db.collection("category").document(id.toString())
            .get()
            .addOnSuccessListener {
                name=it.getString("name").toString()
                binding.edAdd.setText(name)
                hideDialog()
            }
            .addOnFailureListener {
                hideDialog()
                Toast.makeText(d,it.message,Toast.LENGTH_SHORT).show()
            }
    }
    fun setCategory(){
        showDialog()
        var id =sharedP.getString("idCategory","x")
        name=binding.edAdd.text.toString()
        val category=Category("",name)
        db.collection("category").document(id.toString())
            .set(category)
            .addOnSuccessListener {
                hideDialog()
                Toast.makeText(d,"Success Update Category",Toast.LENGTH_SHORT).show()
                (activity as AdminHome).makeCurrentFragment(CategoryFragment())
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
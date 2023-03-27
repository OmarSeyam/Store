package com.example.project.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.project.Login
import com.example.project.activity.*
import com.example.project.databinding.FragmentAdminProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminProfileFragment : Fragment() {
    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var progressDialog: ProgressDialog? = null
    lateinit var d: Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        d = (activity as AdminHome)
        showDialog()
        val db = FirebaseFirestore.getInstance()
        val sharedP = requireActivity().getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        val edit = sharedP.edit()
        val email = sharedP.getString("email", "null")
        val id = sharedP.getString("idAdmin", "null")
        db.collection("admin").document(id.toString())
            .get()
            .addOnSuccessListener {
                binding.edEmail.setText(it.get("email").toString())
                binding.edPassward.setText(it.get("password").toString())
                binding.edName.setText(it.get("name").toString())
                binding.edPhone.setText(it.get("phone").toString())
            }
            .addOnFailureListener {
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
        hideDialog()
        binding.btnUpdate.setOnClickListener {
            val i = Intent(d, EditAdminProfile::class.java)
            startActivity(i)
        }
        binding.btnLogeOut.setOnClickListener {
            val dialog = AlertDialog.Builder(d)
            dialog.setTitle("Log Out")
            dialog.setMessage("Are you sure!")
            dialog.setPositiveButton("Yes") { _, _ ->
                edit.putBoolean("isLogina", false)
                edit.apply()
                var i = Intent(activity, Adminlogin::class.java)
                startActivity(i)
                activity?.finish()
            }
            dialog.setNegativeButton("No") { d, _ ->
                d.dismiss()
            }
            dialog.create().show()
        }
    }

    override fun onStart() {
        showDialog()
        val db = FirebaseFirestore.getInstance()
        val sharedP = requireActivity().getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        val id = sharedP.getString("idAdmin", "null")
        db.collection("admin").document(id.toString())
            .get()
            .addOnSuccessListener {
                binding.edEmail.setText(it.get("email").toString())
                binding.edPassward.setText(it.get("password").toString())
                binding.edName.setText(it.get("name").toString())
                binding.edPhone.setText(it.get("phone").toString())
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }

        super.onStart()
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
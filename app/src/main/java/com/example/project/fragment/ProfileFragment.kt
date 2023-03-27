package com.example.project.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.project.Login
import com.example.project.R
import com.example.project.activity.EditProfile
import com.example.project.activity.Home
import com.example.project.databinding.FragmentBasketBinding
import com.example.project.databinding.FragmentProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var progressDialog: ProgressDialog? = null
    lateinit var d:Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        d = (activity as Home)
        showDialog()
        val db = FirebaseFirestore.getInstance()
        val sharedP = requireActivity().getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        val edit = sharedP.edit()
        val id = sharedP.getString("id", "null")
        db.collection("customer").document(id.toString())
            .get()
            .addOnSuccessListener {
                    binding.edEmail.setText(it.get("email").toString())
                    binding.edPassward.setText(it.get("password").toString())
                    binding.edName.setText(it.get("name").toString())
            }
            .addOnFailureListener {
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
        hideDialog()
        binding.btnUpdate.setOnClickListener {
            val i =Intent(d,EditProfile::class.java)
            startActivity(i)
        }
        binding.btnLogeOut.setOnClickListener {
            val dialog = AlertDialog.Builder(d)
            dialog.setTitle("Log Out")
            dialog.setMessage("Are you sure!")
            dialog.setPositiveButton("Yes") { _, _ ->
                edit.putBoolean("isLogin", false)
                edit.apply()
                var i = Intent(activity, Login::class.java)
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
        val id = sharedP.getString("id", "null")
        db.collection("customer").document(id.toString())
            .get()
            .addOnSuccessListener {
                binding.edEmail.setText(it.get("email").toString())
                binding.edPassward.setText(it.get("password").toString())
                binding.edName.setText(it.get("name").toString())
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
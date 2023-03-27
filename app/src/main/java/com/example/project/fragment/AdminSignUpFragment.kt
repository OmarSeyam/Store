package com.example.project.fragment

import android.app.Activity
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
import com.example.project.activity.AdminHome
import com.example.project.activity.Adminlogin
import com.example.project.activity.Home
import com.example.project.databinding.FragmentAdminSignUpBinding
import com.example.project.model.Admin
import com.google.firebase.firestore.FirebaseFirestore

class AdminSignUpFragment : Fragment() {
    private var _binding: FragmentAdminSignUpBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        d = (activity as Adminlogin)
        binding.button.setOnClickListener {
            var isExistE = false
            var isExistP = false
            var email = binding.edEmail.text.toString()
            var name = binding.edName.text.toString()
            var password = binding.edPassword.text.toString()
            var phone = binding.edPhone.text.toString()
            var passwordCount = password.length
            if (email.isNotEmpty() &&
                name.isNotEmpty() && password.isNotEmpty()&&phone.isNotEmpty()
            ) {
                showDialog()
                db.collection("admin")
                    .get()
                    .addOnSuccessListener { task ->
                        for (documnent in task) {
                            if (email.equals(documnent.getString("email")))
                                isExistE = true
                        }
                        db.collection("admin")
                            .get()
                            .addOnSuccessListener { task ->
                                for (documnent in task) {
                                    if (password.equals(documnent.getString("password")))
                                        isExistP = true
                                }
                                if (email.isNotEmpty() &&
                                    name.isNotEmpty() && password.isNotEmpty()&&phone.isNotEmpty()
                                ) {
                                    if (passwordCount >= 6 && email.contains(
                                            "@"
                                        ) && email.contains(
                                            ".com"
                                        ) && !isExistE && !isExistP
                                    ) {
                                        var admin =Admin("",name,phone,email,password)
                                        db.collection("admin")
                                            .add(admin)
                                            .addOnSuccessListener {
                                                hideDialog()
                                                var sharedP =
                                                    requireActivity().getSharedPreferences(
                                                        "MyPref1",
                                                        Context.MODE_PRIVATE
                                                    )
                                                var edit = sharedP!!.edit()
                                                edit.putString("idAdmin", it.id)
                                                edit.apply()
                                                val i = Intent(d, AdminHome::class.java)
                                                startActivity(i)
                                                activity?.finish()
                                                Toast.makeText(
                                                    d,
                                                    "Add Successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener {
                                                hideDialog()
                                                it
                                                Toast.makeText(
                                                    d,
                                                    "Add Fail(${it.message})",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        if (passwordCount < 6)
                                            binding.edPassword.setError("min 6 charectors")
                                        if (isExistE)
                                            binding.edEmail.setError("This email is used")
                                        if (!email.contains("@") || !email.contains(".com"))
                                            binding.edEmail.setError("wrong value")
                                        if (isExistP)
                                            binding.edPassword.setError("This password is used")
                                    }
                                } else {
                                    if (email.isEmpty() || name.isEmpty() || password.isEmpty())
                                        Toast.makeText(
                                            activity,
                                            "complete input!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                }
                                hideDialog()
                            }
                    }


            } else {
                Toast.makeText(
                    activity,
                    "complete input!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(Adminlogin@ d)
        progressDialog!!.setMessage("Uploading information ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}
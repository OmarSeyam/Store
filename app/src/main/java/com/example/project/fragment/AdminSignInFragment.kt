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
import com.example.project.databinding.FragmentAdminSignInBinding
import com.google.firebase.firestore.FirebaseFirestore


class AdminSignInFragment : Fragment() {
    private var _binding: FragmentAdminSignInBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var progressDialog: ProgressDialog? = null
    lateinit var d: Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        d = (activity as Adminlogin)
        binding.button.setOnClickListener {
            var email = binding.edEmail.text.toString()
            var password = binding.edPassward.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                showDialog()
                var isExist = false
                db.collection("admin")
                    .get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            if (email.equals(doc.get("email")) && password.equals(doc.get("password"))) {
                                isExist = true
                            }

                            if (email.isNotEmpty() && password.isNotEmpty() && isExist) {
                                val i = Intent(d, AdminHome::class.java)
                                var sharedP = requireActivity().getSharedPreferences(
                                    "MyPref1",
                                    Context.MODE_PRIVATE
                                )
                                var edit = sharedP!!.edit()
                                edit.putString("idAdmin", doc.id)
                                edit.apply()
                                if (binding.checkLogin.isChecked) {
                                    edit.putBoolean("isLogina", true)
                                }
                                edit.apply()
                                startActivity(i)
                                activity?.finish()
                                break

                            } else {
                                if (email.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(activity, "complete input!", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            }
                            hideDialog()
                        }
                        if (!isExist) {
                            Toast.makeText(
                                activity,
                                "wrong in email or password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }else{
                Toast.makeText(d, "complete input!", Toast.LENGTH_SHORT).show()
            }
        }
        var sharedP = requireActivity().getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        if (sharedP!!.getBoolean("isLogina", false)) {
            val i = Intent(d, AdminHome::class.java)
            startActivity(i)
            activity?.finish()
        }
        super.onViewCreated(view, savedInstanceState)

    }

    private fun showDialog() {
        progressDialog = ProgressDialog(Adminlogin@ d)
        progressDialog!!.setMessage("Wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing&&progressDialog !=null)
            progressDialog!!.dismiss()
    }
}
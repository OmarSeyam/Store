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
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.activity.Home
import com.example.project.databinding.FragmentSignInBinding
import com.google.firebase.firestore.FirebaseFirestore

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var progressDialog: ProgressDialog? = null
    lateinit var d: Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        d = (activity as Login)
        binding.button.setOnClickListener {
            var email = binding.edEmail.text.toString()
            var password = binding.edPassward.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                showDialog()
                var isExist = false
                db.collection("customer")
                    .get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            if (email.equals(doc.get("email")) && password.equals(doc.get("password"))) {
                                isExist = true
                            }

                            if (email.isNotEmpty() && password.isNotEmpty() && isExist) {
                                val i = Intent(d, Home::class.java)
                                var sharedP = requireActivity().getSharedPreferences(
                                    "MyPref1",
                                    Context.MODE_PRIVATE
                                )
                                var edit = sharedP!!.edit()
                                edit.putString("id", doc.id)
                                if (binding.checkLogin.isChecked) {
                                    edit.putBoolean("isLogin", true)
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
        if (sharedP!!.getBoolean("isLogin", false)) {
            val i = Intent(d, Home::class.java)
            startActivity(i)
            activity?.finish()
        }
        super.onViewCreated(view, savedInstanceState)

    }

    private fun showDialog() {
        progressDialog = ProgressDialog(Login@ d)
        progressDialog!!.setMessage("Wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing&&progressDialog !=null)
            progressDialog!!.dismiss()
    }

}
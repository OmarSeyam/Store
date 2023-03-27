package com.example.project.activity

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project.R
import com.example.project.databinding.ActivityEditProfileBinding
import com.example.project.model.Customer
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showDialog()
    }
    override fun onStart() {
        val db = FirebaseFirestore.getInstance()
        val sharedP = getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        val edit = sharedP.edit()
        val id = sharedP!!.getString("id", "null")
        var oldName = "null"
        var oldEmail ="null"
        var oldPassword = "null"
        db.collection("customer").document(id!!)
            .get()
            .addOnSuccessListener {

                    binding.edEmail.setText(it.get("email").toString())
                    binding.edPassward.setText(it.get("password").toString())
                    binding.edName.setText(it.get("name").toString())
                    oldName = it.get("name").toString()
                    oldEmail =it.get("email").toString()
                    oldPassword = it.get("password").toString()


            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
        hideDialog()
        binding.btnApply.setOnClickListener {
            showDialog()
            var isExistE = false
            var isExistP = false
            val newName = binding.edName.text.toString()
            val newEmail = binding.edEmail.text.toString()
            val newPassword = binding.edPassward.text.toString()
            val passwordCount = newPassword.length
            if (newEmail.contains("@") && newEmail.contains(".com") && passwordCount >= 6) {
                if (newEmail.isNotEmpty() && newName.isNotEmpty() && newPassword.isNotEmpty()) {
                    db.collection("customer")
                        .get()
                        .addOnSuccessListener { task ->
                            for (documnent in task) {
                                if (newEmail.equals(documnent.getString("email")))
                                    isExistE = true
                            }
                            db.collection("customer")
                                .get()
                                .addOnSuccessListener { task ->
                                    for (documnent in task) {
                                        if (newPassword.equals(documnent.getString("password")))
                                            isExistP = true
                                    }

                                    if (oldPassword.equals(newPassword) && !isExistE ||
                                        !isExistP && oldEmail.equals(newEmail) ||
                                        !isExistE && !isExistP
                                        || !oldName.equals(newName) &&
                                        oldEmail.equals(newEmail) && oldPassword.equals(newPassword)
                                    ) {
                                        val customer =Customer("",newName,newEmail,newPassword)
                                        val id = sharedP.getString("id", "null")
                                        db.collection("customer").document(id!!)
                                            .set(customer)
                                            .addOnFailureListener {
                                                Toast.makeText(
                                                    this,
                                                    "Update Fiald",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "update sucssfully",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                                edit.putString("email",newEmail)
                                                edit.apply()
                                                oldName =newName
                                                oldEmail =newEmail
                                                oldPassword = newPassword
                                            }
                                    } else {
                                        if (!oldEmail.equals(newEmail) && isExistE) {
                                            binding.edEmail.setError("This email is used")
                                        }
                                        if (!oldPassword.equals(newPassword) && isExistP) {
                                            binding.edPassward.setError("This password is used")
                                        }
                                    }
                                    hideDialog()
                                }
                        }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        it.message,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                    hideDialog()
                                }

                }else{
                    Toast.makeText(this, "complete input!", Toast.LENGTH_SHORT)
                        .show()
                }
            }else{
                binding.edPassward.setError("min 6 charectors")
                if (!newEmail.contains("@") || !newEmail.contains(".com"))
                    binding.edEmail.setError("wrong value")
            }
        }
        super.onStart()
    }
    private fun showDialog() {
        progressDialog = ProgressDialog(EditProfile@ this)
        progressDialog!!.setMessage("Wait ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}
package com.example.project.activity

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.project.R
import com.example.project.databinding.ActivityEditAdminProfileBinding
import com.example.project.databinding.ActivityEditProfileBinding
import com.example.project.model.Admin
import com.google.firebase.firestore.FirebaseFirestore

class EditAdminProfile : AppCompatActivity() {
    lateinit var binding: ActivityEditAdminProfileBinding
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showDialog()
    }

    override fun onStart() {
        val db = FirebaseFirestore.getInstance()
        val sharedP = getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        var oldName = "null"
        var oldEmail = "null"
        var oldPassword = "null"
        var oldPhone = "null"
        val id = sharedP.getString("idAdmin", "null")
        db.collection("admin").document(id!!)
            .get()
            .addOnSuccessListener {

                    binding.edEmail.setText(it.get("email").toString())
                    binding.edPassward.setText(it.get("password").toString())
                    binding.edName.setText(it.get("name").toString())
                    binding.edPhone.setText(it.get("phone").toString())
                    oldName = it.get("name").toString()
                    oldEmail = it.get("email").toString()
                    oldPassword = it.get("password").toString()
                    oldPhone = it.get("phone").toString()


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
            val newPhone = binding.edPhone.text.toString()
            val passwordCount = newPassword.length
            if (newEmail.contains("@") && newEmail.contains(".com") && passwordCount >= 6) {
                if (newEmail.isNotEmpty() && newName.isNotEmpty() && newPassword.isNotEmpty()) {
                    db.collection("admin")
                        .get()
                        .addOnSuccessListener { task ->
                            for (documnent in task) {
                                if (newEmail.equals(documnent.getString("email")))
                                    isExistE = true
                            }
                            db.collection("admin")
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
                                        oldEmail.equals(newEmail) && oldPassword.equals(newPassword) ||
                                        !oldPhone.equals(newPhone) &&
                                        oldEmail.equals(newEmail) && oldPassword.equals(newPassword)
                                    ) {
                                        val admin =Admin("",newName,newPhone,newEmail,newPassword)
                                        db.collection("admin").document(id!!)
                                            .set(admin)
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
                                                oldName =newName
                                                oldEmail =newEmail
                                                oldPassword = newPassword
                                                oldPhone = newPhone
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

                } else {
                    Toast.makeText(this, "complete input!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.edPassward.setError("min 6 charectors")
                if (!newEmail.contains("@") || !newEmail.contains(".com"))
                    binding.edEmail.setError("wrong value")
            }
        }
        super.onStart()
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Wait ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }
}
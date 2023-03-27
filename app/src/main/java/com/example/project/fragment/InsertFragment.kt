package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.databinding.FragmentInsertBinding
import com.example.project.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class InsertFragment : Fragment() {
    private var _binding: FragmentInsertBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsertBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var progressDialog: ProgressDialog? = null
    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    lateinit var d: Activity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        d = (activity as AdminHome)
        var category1=" "
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        binding.imageProduct.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        val categoryName: ArrayList<String> = ArrayList()
        val categoryId: ArrayList<String> = ArrayList()
        db.collection("category")
            .get()
            .addOnSuccessListener {
                for(doc in it){
                    categoryName.add(doc.getString("name").toString())
                    categoryId.add(doc.id)
                }
                val arrayAdapter =
                    ArrayAdapter<String>(d,android.R.layout.simple_spinner_item,categoryName)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter=arrayAdapter
                binding.spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>,
                                                view: View, position: Int, id: Long) {
                        category1=categoryId[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }

        binding.btnAdd.setOnClickListener {
            var name = binding.edName.text.toString()
            var price = binding.price.text.toString()
            var copies = binding.copies.text.toString()
            var details = binding.eddetails.text.toString()
            if (name.isNotEmpty() && price.isNotEmpty() && copies.isNotEmpty()
                && details.isNotEmpty() && imageURI != null&&category1 !=" "
            ) {
                showDialog()
                val bitmap = (binding.imageProduct.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()

                val childRef =
                    imageRef.child(System.currentTimeMillis().toString() + "_hzmimages.png")
                var uploadTask = childRef.putBytes(data)
                uploadTask.addOnFailureListener { exception ->
                    Toast.makeText(
                        d,
                        "Image Uploaded Fail(${exception.message})",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    hideDialog()
                }.addOnSuccessListener {
                    childRef.downloadUrl.addOnSuccessListener { uri ->
                        fileURI = uri

                        if (name.isNotEmpty() && price.isNotEmpty() && copies.isNotEmpty()
                            && details.isNotEmpty() && fileURI != null
                        ) {
                                    var price1 = price.toDouble()
                                    var product = Product(
                                        "",
                                        name,
                                        fileURI.toString(),
                                        details,
                                        category1,
                                        price1,
                                        0,
                                        copies,
                                        0.0f,
                                        0
                                    )
                                    db.collection("product")
                                        .add(product)
                                        .addOnSuccessListener {
                                            binding.edName.text.clear()
                                            binding.eddetails.text.clear()
                                            binding.copies.text.clear()
                                            binding.price.text.clear()
                                            binding.imageProduct.setImageResource(R.drawable.ic_baseline_insert_photo_24)
                                            Toast.makeText(
                                                d,
                                                "Success Add This Product",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            hideDialog()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                d,
                                                "Fail Add This Product(${it.message})",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            hideDialog()
                                        }

                        } else {
                            Toast.makeText(activity, "complete input!", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                super.onViewCreated(view, savedInstanceState)
            } else {
                Toast.makeText(activity, "complete input!", Toast.LENGTH_SHORT).show()

            }
        }
        binding.addCategory.setOnClickListener {
            (activity as AdminHome).makeCurrentFragment(AddCategoryFragment())
        }
    }

    private fun showDialog() {
        progressDialog = ProgressDialog(AdminHome@ d)
        progressDialog!!.setMessage("Uploading Product ...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun hideDialog() {
        if (progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            binding.imageProduct.setImageURI(imageURI)
        }
    }

}
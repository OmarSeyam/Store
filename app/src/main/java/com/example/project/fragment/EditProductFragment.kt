package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.databinding.FragmentDetailsBinding
import com.example.project.databinding.FragmentEditProductBinding
import com.example.project.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class EditProductFragment : Fragment() {
    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var progressDialog: ProgressDialog? = null
    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    var imageURIOld: Uri? = null
    lateinit var sharedP: SharedPreferences
    lateinit var d :Activity
    lateinit var db: FirebaseFirestore
    lateinit var storage: FirebaseStorage
    lateinit var idP: String
    var sold: Int = 0
    var sumRate: Float = 0f
    var numRate: Int = 0
    lateinit var storageRef: StorageReference
    lateinit var imageRef: StorageReference
    var category1 = " "
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d = (activity as AdminHome)
        getAll()
        storage = Firebase.storage
        storageRef = storage.reference
        imageRef = storageRef.child("images")
        binding.imageProduct.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        binding.btnSave.setOnClickListener {
            setAll()
        }
    }

    fun getAll() {
        sharedP = d.getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        idP = sharedP.getString("idProduct", "x").toString()
        db = FirebaseFirestore.getInstance()
        db.collection("product").document(idP)
            .get()
            .addOnSuccessListener {
                binding.tvTitle.setText(it.getString("name"))
                binding.edPrice.setText(it.get("price").toString())
                binding.edQty.setText(it.get("numOfCopies").toString())
                binding.eddetails.setText(it.get("detail").toString())
                imageURIOld = it.get("img").toString().toUri()
                sold = it.get("numOfCopySold").toString().toInt()
                numRate = it.get("numRate").toString().toInt()
                sumRate = it.get("sumRate").toString().toFloat()
                Picasso.get().load(it.get("img").toString()).into(binding.imageProduct)
            }
            .addOnFailureListener {
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
            }
        val categoryName: ArrayList<String> = ArrayList()
        val categoryId: ArrayList<String> = ArrayList()
        db.collection("category")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    categoryName.add(doc.getString("name").toString())
                    categoryId.add(doc.id)
                }
                val arrayAdapter =
                    ArrayAdapter<String>(d, android.R.layout.simple_spinner_item, categoryName)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = arrayAdapter
                binding.spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        category1 = categoryId[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun setAll() {
        var name = binding.tvTitle.text.toString()
        var price = binding.edPrice.text.toString()
        var details = binding.eddetails.text.toString()
        var qty = binding.edQty.text.toString()

        if (name.isNotEmpty() && price.isNotEmpty() && qty.isNotEmpty()
            && details.isNotEmpty() && category1 != " "
        ) {
            showDialog()
            if(imageURI!=null) {
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

                        if (name.isNotEmpty() && price.isNotEmpty() && qty.isNotEmpty()
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
                                sold,
                                qty,
                                sumRate,
                                numRate
                            )
                            db.collection("product").document(idP)
                                .set(product)
                                .addOnSuccessListener {
                                    binding.imageProduct.setImageResource(R.drawable.ic_baseline_insert_photo_24)
                                    Toast.makeText(
                                        d,
                                        "Success Update This Product",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    hideDialog()
                                    (activity as AdminHome).makeCurrentFragment(DetailsAdminFragment())
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
            }else{
                if (name.isNotEmpty() && price.isNotEmpty() && qty.isNotEmpty()
                    && details.isNotEmpty()
                ) {
                    var price1 = price.toDouble()
                    var product = Product(
                        "",
                        name,
                        imageURIOld.toString(),
                        details,
                        category1,
                        price1,
                        sold,
                        qty,
                        sumRate,
                        numRate
                    )
                    db.collection("product").document(idP)
                        .set(product)
                        .addOnSuccessListener {
                            binding.imageProduct.setImageResource(R.drawable.ic_baseline_insert_photo_24)
                            Toast.makeText(
                                d,
                                "Success Update This Product",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            hideDialog()
                            (activity as AdminHome).makeCurrentFragment(DetailsAdminFragment())
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

        } else {
            Toast.makeText(activity, "complete input!", Toast.LENGTH_SHORT).show()

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
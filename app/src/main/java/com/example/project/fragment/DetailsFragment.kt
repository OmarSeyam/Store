package com.example.project.fragment

import android.app.Activity
import android.app.AlertDialog
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
import com.example.project.activity.Home
import com.example.project.databinding.FragmentDetailsBinding
import com.example.project.databinding.FragmentHomeBinding
import com.example.project.model.Product
import com.example.project.model.isBuyed
import com.example.project.model.isRated
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    lateinit var d: Activity
    lateinit var db: FirebaseFirestore
    lateinit var sharedP: SharedPreferences
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    lateinit var id: String
    lateinit var idP: String
    lateinit var name: String
    lateinit var image: String
    lateinit var detail: String
    lateinit var category: String
    var price1: Double=0.0
    var copies: Int=0
    var price: Double = 0.0
    var sold: Int = 0
    var quantity: Int = 0
    var sumRate: Float = 0f
    var numRate: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        d = (activity as Home)
        sharedP = d.getSharedPreferences("MyPref1", Context.MODE_PRIVATE)
        getProduct()
        binding.btnAdd.setOnClickListener {
            addToCart()
        }

    }

    fun getProduct() {
        showDialog()
        id = sharedP.getString("id", "").toString()
        idP = sharedP.getString("idProduct", "x").toString()
        db.collection("product").document(idP)
            .get()
            .addOnSuccessListener {
                name = it.getString("name").toString()
                image = it.getString("img").toString()
                detail = it.getString("detail").toString()
                category = it.getString("category").toString()
                price = it.getDouble("price").toString().toDouble()
                sold = it.get("numOfCopySold").toString().toInt()
                quantity = it.get("numOfCopies").toString().toInt()
                sumRate = it.get("sumRate").toString().toFloat()
                numRate = it.get("numRate").toString().toInt()
                binding.tvTitle.setText(name)
                binding.eddetails.setText(detail)
                binding.edSales.setText(sold.toString())
                Picasso.get().load(image).into(binding.imageProduct)
                binding.edPrice.setText(price.toString())
                binding.edQty.setText(quantity.toString())
                binding.ednumRate.setText(numRate.toString())
                db.collection("category").document(category.toString())
                    .get()
                    .addOnSuccessListener {
                        binding.edCategory.setText(it.getString("name"))
                    }
                    .addOnFailureListener {
                        binding.edCategory.setText("Category Not Found")
                    }
                hideDialog()
                var x = 1
                db.collection("isRated").whereEqualTo("customer_id", id)
                    .get()
                    .addOnSuccessListener {
                        for (doc in it) {
                            if (doc.getString("product_id") == idP) {
                                binding.ratingBar.rating = doc.get("rate").toString().toFloat()
                                x = 0
                            }
                        }
                        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                            if (x == 1) {
                                val product = Product(
                                    "",
                                    name,
                                    image,
                                    detail,
                                    category,
                                    price,
                                    sold,
                                    quantity,
                                    sumRate + rating,
                                    numRate + 1
                                )
                                db.collection("product").document(idP)
                                    .set(product)
                                    .addOnSuccessListener {
                                        val isRated = isRated("", id, idP, rating)
                                        db.collection("isRated")
                                            .add(isRated)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    d,
                                                    "Rate $rating",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(d, it.message, Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                                    }

                                x = 0
                            }

                        }

                    }


            }
            .addOnFailureListener {
                hideDialog()
                Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
            }

    }

    fun addToCart() {
        showDialog()
        var idIsb:String
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("ADD TO CART")
        dialog.setMessage("Do You Want Sale this Product!")
        dialog.setPositiveButton("yes") { _, _ ->
            var x = 1
            db.collection("isBuyed").whereEqualTo("customer_id", id)
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc.getString("product_id") == idP) {
                            copies =doc.get("numOfCopy").toString().toInt()
                            idIsb=doc.id
                            copies +=1
                            price1=copies*price
                            val product = Product(
                                "",
                                name,
                                image,
                                detail,
                                category,
                                price,
                                sold+1,
                                quantity-1,
                                sumRate ,
                                numRate
                            )
                            var isBuyed=isBuyed("",name,image,id,idP,price1,copies)
                            db.collection("product").document(idP)
                                .set(product)
                                .addOnSuccessListener {
                                    db.collection("isBuyed").document(idIsb)
                                        .set(isBuyed)
                                        .addOnSuccessListener {
                                            Toast.makeText(d,"Success Add To Cart",Toast.LENGTH_SHORT).show()
                                            hideDialog()

                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(d,it.message,Toast.LENGTH_SHORT).show()
                                            hideDialog()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(d,it.message,Toast.LENGTH_SHORT).show()
                                    hideDialog()
                                }
                            x=0
                        }
                    }

                        if (x == 1) {
                            val product = Product(
                                "",
                                name,
                                image,
                                detail,
                                category,
                                price,
                                sold+1,
                                quantity-1,
                                sumRate ,
                                numRate
                            )
                            db.collection("product").document(idP)
                                .set(product)
                                .addOnSuccessListener {
                                    copies =1
                                    price1=copies*price
                                    val isBuyed = isBuyed("", name,image, id, idP,price1,copies)
                                    db.collection("isBuyed")
                                        .add(isBuyed)
                                        .addOnSuccessListener {
                                            Toast.makeText(d, "Success Add To Cart", Toast.LENGTH_SHORT)
                                                .show()
                                            hideDialog()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                                            hideDialog()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(d, it.message, Toast.LENGTH_SHORT).show()
                                    hideDialog()
                                }
                            x = 0
                        }

                    }
                .addOnFailureListener {
                    hideDialog()
                }

                }

        dialog.setNegativeButton("No") { dis, _ ->
            dis.dismiss()
        }
        dialog.create().show()
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

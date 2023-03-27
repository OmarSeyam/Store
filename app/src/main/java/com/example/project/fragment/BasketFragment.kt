package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.Login
import com.example.project.R
import com.example.project.activity.Home
import com.example.project.adapter.BasketAdapter
import com.example.project.adapter.productAdapter
import com.example.project.databinding.FragmentBasketBinding
import com.example.project.databinding.FragmentSignInBinding
import com.example.project.model.isBuyed
import com.google.firebase.firestore.FirebaseFirestore

class BasketFragment : Fragment() {
    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    lateinit var data: ArrayList<isBuyed>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        d = (activity as Home)
        db = FirebaseFirestore.getInstance()
        data = ArrayList()
        getBasket()
    }

    fun getBasket() {
        showDialog()
        db.collection("isBuyed")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    var isBuyed = isBuyed(
                        doc.id,
                        doc.getString("name").toString(),
                        doc.getString("img").toString(),
                        doc.getString("customer_id").toString(),
                        doc.getString("product_id").toString(),
                        doc.get("price").toString().toDouble(),
                        doc.get("numOfCopy").toString().toInt()
                    )
                    data.add(isBuyed)
                }
                var basketAdapter = BasketAdapter(d, data)
                binding.rv.layoutManager = LinearLayoutManager(d)
                binding.rv.adapter = basketAdapter
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d,it.message,Toast.LENGTH_SHORT).show()
                hideDialog()
            }

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
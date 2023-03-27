package com.example.project.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.adapter.BasketAdapter
import com.example.project.adapter.StatisticsAdapter
import com.example.project.databinding.FragmentAdminHomeBinding
import com.example.project.databinding.FragmentStatisticsBinding
import com.example.project.model.Product
import com.example.project.model.isBuyed
import com.google.firebase.firestore.FirebaseFirestore

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }
    private var progressDialog: ProgressDialog? = null
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    lateinit var data: ArrayList<Product>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d = (activity as AdminHome)
        db= FirebaseFirestore.getInstance()
        data= ArrayList()
        getStatistics()
    }



    fun getStatistics() {
        showDialog()
        db.collection("product")
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                             var product = Product(
                                doc.id,
                                doc.getString("name").toString(),
                                doc.getString("img").toString(),
                                doc.getString("detail").toString(),
                                "category",
                                doc.get("price").toString().toDouble(),
                                doc.get("numOfCopySold").toString().toInt(),
                                doc.get("numOfCopies").toString().toInt(),
                                doc.get("sumRate").toString().toFloat(),
                                doc.get("numRate").toString().toInt()
                            )
                            data.add(product)

                }
                val statisticsAdapter = StatisticsAdapter(d, data)
                binding.rv.layoutManager = LinearLayoutManager(d)
                binding.rv.adapter=statisticsAdapter
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(d,it.message, Toast.LENGTH_SHORT).show()
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
package com.example.project.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.project.R
import com.example.project.activity.AdminHome
import com.example.project.databinding.FragmentAddCategoryBinding
import com.example.project.databinding.FragmentAdminHomeBinding
import com.example.project.model.Category
import com.google.firebase.firestore.FirebaseFirestore


class AddCategoryFragment : Fragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        val d = (activity as AdminHome)
        binding.btnAdd.setOnClickListener {
            val category = binding.edAdd.text.toString()
            if (category.isNotEmpty()) {
                db.collection("category")
                    .add(Category("", category))
                    .addOnSuccessListener {
                        Toast.makeText(d, "Success Add This Category", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(d, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }
    }

}
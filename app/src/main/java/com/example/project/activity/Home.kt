package com.example.project.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.project.R
import com.example.project.databinding.ActivityHomeBinding
import com.example.project.fragment.BasketFragment
import com.example.project.fragment.HomeFragment
import com.example.project.fragment.ProfileFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val Home= HomeFragment()
        val Basket= BasketFragment()
        val Profile= ProfileFragment()
        makeCurrentFragment(Home)
        val db = FirebaseFirestore.getInstance()
        val storage=Firebase.storage
        var storageRef = storage.reference
        binding.nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->makeCurrentFragment(Home)
                R.id.basket ->makeCurrentFragment(Basket)
                R.id.profile ->makeCurrentFragment(Profile)
            }
            true
        }
    }
    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment).addToBackStack(null)
            commit()
        }
    }
}
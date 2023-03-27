package com.example.project.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import com.example.project.R
import com.example.project.databinding.ActivityAdminHomeBinding
import com.example.project.databinding.ViewCategoryAdminBinding
import com.example.project.fragment.*

class AdminHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val Home=AdminHomeFragment()
        val Insert=InsertFragment()
        val Statistics=StatisticsFragment()
        val Profile=AdminProfileFragment()
        makeCurrentFragment(Home)
        binding.nav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> makeCurrentFragment(Home)
                R.id.insert -> makeCurrentFragment(Insert)
                R.id.statistics-> makeCurrentFragment(Statistics)
                R.id.profile -> makeCurrentFragment(Profile)
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
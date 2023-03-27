package com.example.project.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.project.Login
import com.example.project.R
import com.example.project.databinding.ActivityAdminloginBinding
import com.example.project.fragment.AdminSignInFragment
import com.example.project.fragment.AdminSignUpFragment

class Adminlogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAdminloginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var isClickSignUp = false
        var isClickSignIn = true
        val signIn = AdminSignInFragment()
        val signUp = AdminSignUpFragment()
        makeCurrentFragment(signIn)
        anim1(binding)
        supportActionBar?.hide()

        binding.txtAdmin.setOnClickListener {
            val i= Intent(this, Login::class.java)
            startActivity(i)
        }

        binding.btnSU.setOnClickListener {
            isClickSignUp = true
            if (isClickSignIn) {
                binding.constraint2.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.right_left
                    )
                )
                isClickSignIn = false
            }
            binding.btnSU.setBackgroundResource(R.drawable.sign_shape2_2)
            binding.btnSU.setTextColor(Color.WHITE)
            binding.btnSI.setBackgroundResource(R.drawable.sign_shape1)
            binding.btnSI.setTextColor(Color.BLACK)
            makeCurrentFragment(signUp)
        }
        binding.btnSI.setOnClickListener {
            isClickSignIn = true
            if (isClickSignUp) {
                binding.constraint2.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.left_right
                    )
                )
                isClickSignUp = false
            }
            binding.btnSU.setBackgroundResource(R.drawable.sign_shape2)
            binding.btnSU.setTextColor(Color.BLACK)
            binding.btnSI.setBackgroundResource(R.drawable.sign_shape1_1)
            binding.btnSI.setTextColor(Color.WHITE)
            makeCurrentFragment(signIn)

        }

    }


    private fun anim1(binding: ActivityAdminloginBinding) {
        binding.constraint2.translationY = 1300F
        binding.linearLayout2.translationY = 1300F
        binding.imageView2.translationY = 1300F
        binding.textView.translationY = 1300F
        binding.constraint2.animate().translationY(0F).alpha(1F).setDuration(1000)
            .setStartDelay(600).start()
        binding.imageView2.animate().translationY(0F).alpha(1F).setDuration(1100)
            .setStartDelay(0).start()
        binding.textView.animate().translationY(0F).alpha(1F).setDuration(1150)
            .setStartDelay(100).start()
        binding.linearLayout2.animate().translationY(0F).alpha(1F).setDuration(1000)
            .setStartDelay(300).start()
    }

    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.constraint2, fragment)
            commit()
        }

    }
}

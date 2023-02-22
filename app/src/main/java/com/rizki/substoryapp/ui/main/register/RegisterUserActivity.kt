package com.rizki.substoryapp.ui.main.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.rizki.substoryapp.R
import com.rizki.substoryapp.databinding.ActivityRegisterUserBinding
import com.rizki.substoryapp.response.ResponseRegister
import com.rizki.substoryapp.retrofit.ConfigApi
import com.rizki.substoryapp.ui.main.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        playAnimation()

        binding.btnRegisterApp.setOnClickListener {
            val name = binding.etRegisterName.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val password = binding.etRegisterPassword.text.toString()

            registerUser(name, email, password)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tittle = ObjectAnimator.ofFloat(binding.tvRegisterTittle, View.ALPHA, 1f).setDuration(500)
        val nameEdit = ObjectAnimator.ofFloat(binding.etRegisterName, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.etRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.etRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegisterApp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially( tittle, nameEdit, emailEdit,  passwordEdit, register)
            startDelay = 500
        }.start()
    }


    private fun registerUser(name: String, email: String, password: String) {
        showsLoading(true)

        val client = ConfigApi.getServiceApi().registerUser(name, email, password)
        client.enqueue(object: Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                responses: Response<ResponseRegister>
            ) {
                showsLoading(false)
                val responsesBody = responses.body()
                Log.d(TAGS, "onResponse: $responsesBody")
                if(responses.isSuccessful && responsesBody?.Message == "User created") {
                    Toast.makeText(this@RegisterUserActivity, getString(R.string.success_register), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterUserActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(TAGS, "onFailure1: ${responses.message()}")
                    Toast.makeText(this@RegisterUserActivity, getString(R.string.failed_register), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                showsLoading(false)
                Log.e(TAGS, "onFailure2: ${t.message}")
                Toast.makeText(this@RegisterUserActivity, getString(R.string.failed_register), Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun showsLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val TAGS = "Register User Activity"
    }

}
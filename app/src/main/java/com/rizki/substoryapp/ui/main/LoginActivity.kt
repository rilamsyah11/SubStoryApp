package com.rizki.substoryapp.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.rizki.substoryapp.R
import com.rizki.substoryapp.databinding.ActivityLoginBinding
import com.rizki.substoryapp.helper.ViewsModelFactory
import com.rizki.substoryapp.model.UsersAuth
import com.rizki.substoryapp.preference.UsersPreference
import com.rizki.substoryapp.response.ResponseLogin
import com.rizki.substoryapp.retrofit.ConfigApi
import com.rizki.substoryapp.ui.main.register.RegisterUserActivity
import com.rizki.substoryapp.ui.main.stories.StoriesActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.DataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: MainViewModel
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewModel()
        playAnimation()

        binding.btnLoginApp.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()

            loginUser(email, password)
        }

        binding.btnRegisterApp.setOnClickListener {
            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogoLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tittle = ObjectAnimator.ofFloat(binding.tvLoginTittle, View.ALPHA, 1f).setDuration(500)
        val emailEdit = ObjectAnimator.ofFloat(binding.etLoginEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEdit = ObjectAnimator.ofFloat(binding.etLoginPassword, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.btnLoginApp, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.btnRegisterApp, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, register)
        }

        AnimatorSet().apply {
            playSequentially(tittle, emailEdit, passwordEdit, together)
            startDelay = 500
        }.start()
    }


    private fun setViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewsModelFactory(UsersPreference.getInstances(DataStore))
        )[MainViewModel::class.java]

        loginViewModel.getUsers().observe(this) { users ->
            if(users.IsLogin) {
                val intent = Intent(this, StoriesActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


    private fun loginUser(email: String, password: String) {
        showsLoading(true)

        val client = ConfigApi.getServiceApi().loginUser(email, password)
        client.enqueue(object: Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, responses: Response<ResponseLogin>) {
                showsLoading(false)
                val responsesBody = responses.body()
                Log.d(TAGS, "onResponse: $responsesBody")
                if(responses.isSuccessful && responsesBody?.Message == "success") {
                    loginViewModel.saveUsers(UsersAuth(responsesBody.ResultLogin.Token, true))
                    Toast.makeText(this@LoginActivity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, StoriesActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e(TAGS, "onFailure1: ${responses.message()}")
                    Toast.makeText(this@LoginActivity, getString(R.string.failed_login), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                showsLoading(false)
                Log.e(TAGS, "onFailure2: ${t.message}")
                Toast.makeText(this@LoginActivity, getString(R.string.failed_login), Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val TAGS = "Login Activity"
    }
}
package com.rizki.substoryapp.ui.main.stories

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rizki.substoryapp.R
import com.rizki.substoryapp.ui.main.MainViewModel
import com.rizki.substoryapp.helper.ViewsModelFactory
import com.rizki.substoryapp.databinding.ActivityStoriesBinding
import com.rizki.substoryapp.preference.UsersPreference
import com.rizki.substoryapp.ui.main.LoadingAdapter
import com.rizki.substoryapp.ui.main.LoginActivity
import com.rizki.substoryapp.ui.main.createstories.CreateStoriesActivity
import com.rizki.substoryapp.ui.main.maps.MapStoriesActivity

private val Context.DataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoriesActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private val storiesViewModel: StoriesViewModel by viewModels {
        StoriesViewModel.ViewModelFactory(this)
    }
    private lateinit var binding: ActivityStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        setViewModel()
        getStory()
    }

    private fun setViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewsModelFactory(UsersPreference.getInstances(DataStore))
        )[MainViewModel::class.java]
    }

    private fun getStory() {
        val storiesAdapter = StoriesAdapter()
        binding.rvStory.adapter = storiesAdapter.withLoadStateFooter(
            footer = LoadingAdapter {
                storiesAdapter.retry()
            }
        )
        mainViewModel.getUsers().observe(this) { usersAuth ->
            if(usersAuth != null) {
                storiesViewModel.story("Bearer " + usersAuth.Token).observe(this) { story ->
                    storiesAdapter.submitData(lifecycle, story)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_create -> {
                val intent = Intent(this, CreateStoriesActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_refresh -> {
                Toast.makeText(this, "Refresh Halaman.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@StoriesActivity, StoriesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            R.id.menu_map -> {
                val intent = Intent(this, MapStoriesActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_logout -> {
                Toast.makeText(this, "Berhasil Logout.", Toast.LENGTH_SHORT).show()
                mainViewModel.logoutUsers()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

}
package com.example.submissionandroidintermediate.userinterface

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.UserPreferences
import com.example.submissionandroidintermediate.adapter.LoadingStateAdapter
import com.example.submissionandroidintermediate.adapter.StoryListAdapter
import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.databinding.ActivityHomePageBinding
import com.example.submissionandroidintermediate.viewmodel.*

class HomePageActivity : AppCompatActivity() {
    private val pref by lazy {
        UserPreferences.getInstance(dataStore)
    }
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var token: String
    private val homePageViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.home)
        ifClicked()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            token = it
            setUserData(it)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setUserData(token: String) {

        val adapter = StoryListAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            })

        homePageViewModel.getPagingStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryDetail) {
                sendSelectedUser(data)
            }
        })
    }

    private fun sendSelectedUser(data: ListStoryDetail) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_STORY, data)
        startActivity(intent)
    }

    private fun ifClicked() {
        binding.btnFloating.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun logout() {
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        loginViewModel.clearDataLogin()
        Toast.makeText(this, R.string.SuccessLogout, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.changeLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                showAlertDialog()
                true
            }
            R.id.mapsNavigate -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logoutDescription))
            .setPositiveButton(getString(R.string.cancelLogout)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.yes)) { _, _ ->
                logout()
            }
            .show()
    }
}
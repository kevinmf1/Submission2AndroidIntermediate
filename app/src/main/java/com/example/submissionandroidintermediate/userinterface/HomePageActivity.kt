package com.example.submissionandroidintermediate.userinterface

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.UserPreferences
import com.example.submissionandroidintermediate.adapter.StoryListAdapter
import com.example.submissionandroidintermediate.databinding.ActivityHomePageBinding
import com.example.submissionandroidintermediate.dataclass.StoryDetail
import com.example.submissionandroidintermediate.viewmodel.HomePageViewModel
import com.example.submissionandroidintermediate.viewmodel.UserLoginViewModel
import com.example.submissionandroidintermediate.viewmodel.ViewModelFactory


class HomePageActivity : AppCompatActivity() {
    private val pref = UserPreferences.getInstance(dataStore)
    private lateinit var binding: ActivityHomePageBinding
    private lateinit var token: String
    private val homepageViewModel: HomePageViewModel by lazy {
        ViewModelProvider(this)[HomePageViewModel::class.java]
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

        val userLoginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserLoginViewModel::class.java]

        userLoginViewModel.getToken().observe(this) {
            token = it
            homepageViewModel.getStories(token)
        }

        homepageViewModel.message.observe(this) {
            setUserData(homepageViewModel.stories)
            showToast(it)
        }

        homepageViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showToast(msg: String) {
        if (homepageViewModel.isError) {
            Toast.makeText(
                this,
                "${getString(R.string.error_load)} $msg",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showNoData(isNoData: Boolean) {
        binding.noDataFound.visibility = if (isNoData) View.VISIBLE else View.GONE
        binding.tvNoDataFound.visibility = if (isNoData) View.VISIBLE else View.GONE
    }

    private fun setUserData(storyList: List<StoryDetail>) {
        showNoData(storyList.isEmpty())

        val adapter = StoryListAdapter(storyList)
        binding.rvStories.adapter = adapter

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryDetail) {
                sendSelectedUser(data)
            }
        })
    }

    private fun sendSelectedUser(data: StoryDetail) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_STORY, data)
        startActivity(intent)
    }

    private fun ifClicked() {
        binding.btnFloating.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.pullRefresh.setOnRefreshListener {
            homepageViewModel.getStories(token)
            binding.pullRefresh.isRefreshing = false
        }
    }

    private fun logout() {
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserLoginViewModel::class.java]
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
            R.id.refreshApp -> {
                homepageViewModel.getStories(token)
                true
            }
            R.id.changeLanguage -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.logout -> {
                showAlertDialog()
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
            .setNegativeButton(getString(R.string.logoutButton)) { _, _ ->
                logout()
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
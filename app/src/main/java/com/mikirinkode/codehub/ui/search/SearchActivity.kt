package com.mikirinkode.codehub.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikirinkode.codehub.R
import com.mikirinkode.codehub.data.model.User
import com.mikirinkode.codehub.databinding.ActivitySearchBinding
import com.mikirinkode.codehub.ui.detailuser.DetailUserActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchResultViewModel: SearchResultViewModel
    private lateinit var searchResultAdapter: SearchResultAdapter

    private var etQuery : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        searchResultAdapter = SearchResultAdapter()
        searchResultViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[SearchResultViewModel::class.java]
        searchResultAdapter.notifyDataSetChanged()

        binding.apply {
            rvUsers.layoutManager = LinearLayoutManager(this@SearchActivity)
            rvUsers.setHasFixedSize(true)
            rvUsers.adapter = searchResultAdapter

        }

        searchResultViewModel.isLoading.observe(this, {
            showLoading(it)
        })
        searchResultViewModel.onFailure.observe(this, {
            onFailure(it)
        })
        searchResultViewModel.totalUserFound.observe(this, {
            totalUserCheck(it)
        })

        searchResultAdapter.setOnItemClickCallback(object : SearchResultAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                Intent(this@SearchActivity, DetailUserActivity::class.java).also {
                    it.putExtra(DetailUserActivity.EXTRA_USERNAME, data.login)
                    it.putExtra(DetailUserActivity.EXTRA_ID, data.id)
                    it.putExtra(DetailUserActivity.EXTRA_AVATAR_URL, data.avatarUrl)
                    it.putExtra(DetailUserActivity.EXTRA_HTML_URL, data.htmlUrl)
                    startActivity(it)
                }
            }

        })



        searchResultViewModel.getSearchUsers().observe(this, {
            if (it != null) {
                searchResultAdapter.setList(it)
            }
        })
        refreshApp()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setIconifiedByDefault(false)
        searchView.onActionViewExpanded()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                etQuery = query
                searchUser()
                return true

            }
            override fun onQueryTextChange(newText: String): Boolean {
                etQuery = newText
                searchUser()
                return false
            }
        })
        return true
    }

    private fun searchUser() {
        binding.apply {
            rvUsers.adapter = searchResultAdapter
            searchResultAdapter.clearList()
            searchResultViewModel.setSearchUser(etQuery)
        }
    }

    private fun totalUserCheck(userFound: Int?) {
        binding.apply {
            if(userFound == 0){
                tvNoData.visibility = View.VISIBLE
            } else {
                tvNoData.visibility = View.GONE
            }
        }
    }
    private fun refreshApp() {
        binding.apply {
            swipeToRefresh.setOnRefreshListener {
                searchResultViewModel.setSearchUser(etQuery)
                swipeToRefresh.isRefreshing = false
            }
        }
    }
    private fun showLoading(state: Boolean) {
        binding.apply {
            if (state) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun onFailure(fail: Boolean) {
        binding.apply {
            if (fail) {
                tvOnFailMsg.visibility = View.VISIBLE
            } else {
                tvOnFailMsg.visibility = View.GONE
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
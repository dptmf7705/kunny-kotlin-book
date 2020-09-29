package com.androidhuman.example.simplegithub.ui.main

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.model.GithubRepo
import com.androidhuman.example.simplegithub.extensions.AutoActivatedDisposable
import com.androidhuman.example.simplegithub.extensions.AutoClearedDisposable
import com.androidhuman.example.simplegithub.extensions.plusAssign
import com.androidhuman.example.simplegithub.ui.repo.RepositoryActivity
import com.androidhuman.example.simplegithub.ui.search.SearchActivity
import com.androidhuman.example.simplegithub.ui.search.SearchAdapter
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainActivity :
    DaggerAppCompatActivity(),
    SearchAdapter.ItemClickListener {

    @Inject
    lateinit var adapter: SearchAdapter

    internal val disposables = AutoClearedDisposable(this)

    internal val viewDisposable = AutoClearedDisposable(
        lifecycleOwner = this,
        alwaysClearOnStop = false
    )

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycle += disposables
        lifecycle += viewDisposable
        lifecycle += AutoActivatedDisposable(this) {
            viewModel.searchHistory
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    with(adapter) {
                        if (it.isNotEmpty) setItems(it.value)
                        else clearItems()
                        notifyDataSetChanged()
                    }
                }
        }

        btnActivityMainSearch.setOnClickListener {
            startActivity<SearchActivity>()
        }

        with(rvActivityMainList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewDisposable += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isNotEmpty) showMessage(it.value)
                else hideMessage()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_main_clear_all == item.itemId) {
            disposables += viewModel.clearSearchHistory()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMessage(message: String) =
        with(tvActivityMainMessage) {
            text = message
            visibility = View.VISIBLE
        }

    private fun hideMessage() =
        with(tvActivityMainMessage) {
            text = ""
            visibility = View.GONE
        }

    override fun onItemClick(repository: GithubRepo) =
        startActivity<RepositoryActivity>(
            RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
            RepositoryActivity.KEY_REPO_NAME to repository.name
        )
}
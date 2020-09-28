package com.androidhuman.example.simplegithub.ui.search

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.model.GithubRepo
import com.androidhuman.example.simplegithub.api.provideGithubApi
import com.androidhuman.example.simplegithub.data.provideSearchHistoryDao
import com.androidhuman.example.simplegithub.extensions.AutoClearedDisposable
import com.androidhuman.example.simplegithub.extensions.plusAssign
import com.androidhuman.example.simplegithub.ui.repo.RepositoryActivity
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChangeEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity

class SearchActivity : AppCompatActivity(), SearchAdapter.ItemClickListener {

    internal lateinit var menuSearch: MenuItem

    internal lateinit var searchView: SearchView

    internal val adapter by lazy {
        SearchAdapter().apply { setItemClickListener(this@SearchActivity) }
    }

    internal val disposables = AutoClearedDisposable(this)

    // 뷰 이벤트에 대한 disposable 객체는 따로 관리
    // 액티비티가 완전 종료되기 전까지 계속 처리해야 함
    internal val viewDisposables = AutoClearedDisposable(
        lifecycleOwner = this,
        alwaysClearOnStop = false
    )

    internal val viewModelFactory by lazy {
        SearchViewModelFactory(
            provideGithubApi(this),
            provideSearchHistoryDao(this)
        )
    }

    lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProviders.of(
            this, viewModelFactory
        )[SearchViewModel::class.java]

        // 라이프사이클 옵저버 등록
        lifecycle += disposables
        lifecycle += viewDisposables

        with(rvActivitySearchList) {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }

        viewDisposables += viewModel.searchResult
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                with(adapter) {
                    if (it.isNotEmpty) setItems(it.value)
                    else clearItems()
                    notifyDataSetChanged()
                }
            }

        viewDisposables += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isNotEmpty) showError(it.value)
                else hideError()
            }

        viewDisposables += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) showProgress()
                else hideProgress()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_search, menu)

        menuSearch = menu.findItem(R.id.menu_activity_search_query)
        searchView = menuSearch.actionView as SearchView

        viewDisposables += searchView.queryTextChangeEvents()
            // 검색 버튼이 눌렸을 때만 이벤트 처리
            .filter { it.isSubmitted }
            // 이벤트에서 검색에 텍스트 추출
            .map { it.queryText() }
            // 빈 문자열이 아닌 경우만 처리
            .filter { it.isNotEmpty() }
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateTitle(it)
                hideSoftKeyboard()
                collapseSearchView()
                searchRepository(it)
            }

        viewDisposables += viewModel.lastSearchKeyword
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isNotEmpty) updateTitle(it.value)
                else menuSearch.expandActionView()
            }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.menu_activity_search_query == item.itemId) {
            item.expandActionView()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(repository: GithubRepo) {
        disposables += viewModel.addToSearchHistory(repository)
        startActivity<RepositoryActivity>(
            RepositoryActivity.KEY_USER_LOGIN to repository.owner.login,
            RepositoryActivity.KEY_REPO_NAME to repository.name
        )
    }

    private fun searchRepository(query: String) {
        disposables += viewModel.searchRepository(query)
    }

    private fun updateTitle(query: String) {
        supportActionBar?.run { subtitle = query }
    }

    private fun hideSoftKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun collapseSearchView() {
        menuSearch.collapseActionView()
    }

    private fun clearResults() {
        with(adapter) {
            clearItems()
            notifyDataSetChanged()
        }
    }

    private fun showProgress() {
        pbActivitySearch.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivitySearch.visibility = View.GONE
    }

    private fun showError(message: String?) {
        with(tvActivitySearchMessage) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        with(tvActivitySearchMessage) {
            text = ""
            visibility = View.GONE
        }
    }
}
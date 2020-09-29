package com.androidhuman.example.simplegithub.ui.repo

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.androidhuman.example.simplegithub.R
import com.androidhuman.example.simplegithub.api.provideGithubApi
import com.androidhuman.example.simplegithub.extensions.AutoClearedDisposable
import com.androidhuman.example.simplegithub.extensions.plusAssign
import com.androidhuman.example.simplegithub.ui.GlideApp
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_repository.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RepositoryActivity : AppCompatActivity() {

    companion object {

        const val KEY_USER_LOGIN = "user_login"

        const val KEY_REPO_NAME = "repo_name"
    }

    internal val dateFormatInResponse = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault()
    )

    internal val dateFormatToShow = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
    )

    internal val disposables = AutoClearedDisposable(this)

    internal val viewDisposable = AutoClearedDisposable(
        lifecycleOwner = this,
        alwaysClearOnStop = false
    )

    internal val viewModelFactory by lazy {
        RepositoryViewModelFactory(provideGithubApi(this))
    }

    lateinit var viewModel: RepositoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        viewModel = ViewModelProviders.of(
            this, viewModelFactory
        )[RepositoryViewModel::class.java]

        lifecycle += disposables
        lifecycle += viewDisposable

        viewDisposable += viewModel.repository
            .filter { it.isNotEmpty }
            .map { it.value }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { repo ->
                GlideApp.with(this@RepositoryActivity)
                    .load(repo.owner.avatarUrl)
                    .into(ivActivityRepositoryProfile)

                tvActivityRepositoryName.text = repo.fullName
                tvActivityRepositoryStars.text = resources
                    .getQuantityString(R.plurals.star, repo.stars, repo.stars)

                if (null == repo.description) {
                    tvActivityRepositoryDescription.setText(R.string.no_description_provided)
                } else {
                    tvActivityRepositoryDescription.text = repo.description
                }

                if (null == repo.language) {
                    tvActivityRepositoryLanguage
                        .setText(R.string.no_language_specified)
                } else {
                    tvActivityRepositoryLanguage.text =
                        repo.language
                }

                try {
                    val lastUpdate = dateFormatInResponse.parse(repo.updatedAt)
                    tvActivityRepositoryLastUpdate.text =
                        dateFormatToShow.format(lastUpdate)
                } catch (e: ParseException) {
                    tvActivityRepositoryLastUpdate.text =
                        getString(R.string.unknown)
                }
            }

        viewDisposable += viewModel.message
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { showError(it) }

        viewDisposable += viewModel.isContentVisible
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { setContentVisibility(it) }

        viewDisposable += viewModel.isLoading
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it) showProgress()
                else hideProgress()
            }

        val login = intent.getStringExtra(KEY_USER_LOGIN)
            ?: throw IllegalArgumentException("No login info exists in extras")

        val repo = intent.getStringExtra(KEY_REPO_NAME)
            ?: throw IllegalArgumentException("No repo info exists in extras")

        disposables += viewModel.requestRepositoryInfo(login, repo)
    }

    private fun setContentVisibility(visible: Boolean) {
        llActivityRepositoryContent.visibility =
            if (visible) View.VISIBLE else View.GONE
    }

    private fun showProgress() {
        pbActivityRepository.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        pbActivityRepository.visibility = View.GONE
    }

    private fun showError(message: String?) =
        with(tvActivityRepositoryMessage) {
            text = message ?: "Unexpected error."
            visibility = View.VISIBLE
        }
}
package com.georgcantor.wallpaperapp.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.model.data.CommonPic
import com.georgcantor.wallpaperapp.model.local.Favorite
import com.georgcantor.wallpaperapp.util.Constants.Companion.EXTRA_PIC
import com.georgcantor.wallpaperapp.util.getScreenSize
import com.georgcantor.wallpaperapp.util.openActivity
import com.georgcantor.wallpaperapp.util.shortToast
import com.georgcantor.wallpaperapp.util.showDialog
import com.georgcantor.wallpaperapp.view.adapter.FavoriteAdapter
import com.georgcantor.wallpaperapp.viewmodel.FavoriteViewModel
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_favorite.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FavoriteActivity : AppCompatActivity() {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private lateinit var gridLayoutManager: RecyclerView.LayoutManager
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(favToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linearLayoutManager = LinearLayoutManager(this)

        gridLayoutManager = StaggeredGridLayoutManager(getScreenSize(), StaggeredGridLayoutManager.VERTICAL)

        viewModel = getViewModel { parametersOf(this) }
    }

    override fun onResume() {
        super.onResume()
        disposable.add(
            viewModel.getFavorites()
                .subscribe({
                    val isNotGrid = it.size in 1..4
                    setupRecyclerView(if (isNotGrid) linearLayoutManager else gridLayoutManager)
                    favRecyclerView.adapter =
                        FavoriteAdapter(this, isNotGrid, it, { fav: Favorite ->
                            val hitJson = fav.hit
                            val pic = Gson().fromJson(hitJson, CommonPic::class.java)
                            openActivity(DetailsActivity::class.java) {
                                putParcelable(
                                    EXTRA_PIC,
                                    CommonPic(
                                        url = pic.url,
                                        width = pic.width,
                                        heght = pic.heght,
                                        favorites = pic.favorites,
                                        tags = pic.tags,
                                        downloads = pic.downloads,
                                        imageURL = pic.imageURL,
                                        fullHDURL = pic.fullHDURL,
                                        user = pic.user,
                                        userImageURL = pic.userImageURL
                                    )
                                )
                            }
                        }) { fav: Favorite ->
                            showDialog(getString(R.string.del_from_fav_dialog)) {
                                viewModel.deleteByUrl(fav.url)
                                recreate()
                            }
                        }
                }, {
                    shortToast(getString(R.string.something_went_wrong))
                })
        )
        viewModel.isEmptyAnimVisible(emptyAnimationView)
        invalidateOptionsMenu()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
    }

    @SuppressLint("CheckResult")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favorite, menu)
        val menuItem = menu.findItem(R.id.action_remove_all)

        viewModel.dbIsNotEmpty()
            .subscribe({
                menuItem.isVisible = it
            }, {
            })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right)
            }
            R.id.action_remove_all -> {
                showDialog(getString(R.string.remove_fav_dialog_message), ::deleteAll)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun setupRecyclerView(manager: RecyclerView.LayoutManager) {
        favRecyclerView.setHasFixedSize(true)
        favRecyclerView.layoutManager = manager
    }

    private fun deleteAll() {
        viewModel.deleteAll()
    }
}

package com.example.lightupthenews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.lightupthenews.application.app
import com.example.lightupthenews.application.userViewModel
import com.example.lightupthenews.database.ArticleDatabase
import com.example.lightupthenews.database.ArticleDatabaseDao
import com.example.lightupthenews.database.ArticleViewModel
import com.example.lightupthenews.database.ArticleViewModelFactory
import com.example.lightupthenews.databinding.ActivityMainBinding
import com.example.lightupthenews.user.*
import timber.log.Timber

lateinit var articleDao: ArticleDatabaseDao
lateinit var articleViewModelFactory: ArticleViewModelFactory
lateinit var articleViewModel: ArticleViewModel

private lateinit var drawerLayout: DrawerLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("created")

        articleDao = ArticleDatabase.getInstance(app).articleDatabaseDao

        articleViewModelFactory = ArticleViewModelFactory(articleDao, app)

        articleViewModel =
            ViewModelProvider(this, articleViewModelFactory)[ArticleViewModel::class.java]

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout
        val navController =
            this.findNavController(R.id.navHostFragment) //to find the navigation controller object

        NavigationUI.setupActionBarWithNavController(
            this,
            navController,
            drawerLayout
        )

        NavigationUI.setupWithNavController(
            binding.navView,
            navController
        )

        if (userViewModel.currentUserId == NOT_LOGGED && !userViewModel.doNotLogIn) {
            navController.navigate(R.id.action_searchFragment_to_loginActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}
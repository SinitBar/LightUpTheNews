package com.example.lightupthenews.search

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightupthenews.SearchFragment
import com.example.lightupthenews.network.ArticleInfo
import com.example.lightupthenews.network.NewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * The [ViewModel] that is attached to the [SearchFragment].
 */
class SearchViewModel : ViewModel() {

    private val _recyclerViewProperties = MutableLiveData<List<ArticleInfo>>()

    val recyclerViewArticles: LiveData<List<ArticleInfo>>
        get() = _recyclerViewProperties

    fun initSearchViewModel(context: Context) {
        Timber.i("initSearchViewModel called")
        searchInEverythingByKeyWords(context, "news")
    }

    fun searchInEverythingByKeyWords(context: Context, searchedText: String?) {
        Timber.i("entered in the searchInEverythingByKeyWords")
        searchedText?.let {
            viewModelScope.launch {
                try {
                    val found = withContext(Dispatchers.IO) {
                        NewsApi.retrofitService.getAllNews(it)
                    }
                    Timber.i("launched coroutine in the searchInEverythingByKeyWords")
                    if (found.body() == null) {
                        Timber.i("Nothing found on your request")
                        Toast.makeText(context, "Nothing found on your request", Toast.LENGTH_LONG).show()
                    }
                    found.body()?.let {
                        _recyclerViewProperties.value = it.articles
                    }
                    Timber.i("ok in searchInEverythingByKeyWords")
                } catch (e: Exception) {
                    _recyclerViewProperties.value = emptyList()
                    Timber.i("error in searchInEverythingByKeyWords, e: " + e)
                }
            }
        }
    }
}

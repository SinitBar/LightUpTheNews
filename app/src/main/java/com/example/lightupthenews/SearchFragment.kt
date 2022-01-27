package com.example.lightupthenews

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lightupthenews.adapters.OneNewsCardRecyclerViewAdapter
import com.example.lightupthenews.application.userViewModel
import com.example.lightupthenews.databinding.FragmentSearchBinding
import com.example.lightupthenews.holders.OneNewsCardViewHolder
import com.example.lightupthenews.network.ArticleInfo
import com.example.lightupthenews.search.SearchViewModel
import com.example.lightupthenews.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

lateinit var articlesInRecyclerview: LiveData<List<ArticleInfo>>

class SearchFragment : Fragment(), OneNewsCardViewHolder.OnItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("created")

        val searchFragmentContext = requireNotNull(this).context
        Timber.i("searchFragmentContext is $searchFragmentContext")

        Timber.i("addOrLoginUser, currentUserId: ${userViewModel.currentUserId}")

        val binding = DataBindingUtil.inflate<FragmentSearchBinding>(
            inflater, R.layout.fragment_search, container, false
        )

        binding.lifecycleOwner = this

        val searchViewModel: SearchViewModel =
            ViewModelProvider(this)[SearchViewModel::class.java]

        binding.viewModel = searchViewModel

        if (this.context == null)
            Timber.i("searchViewModel wasn't initialized because context of fragment was null")
        this.context?.let { searchViewModel.initSearchViewModel(it) }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        binding.recyclerView.adapter = OneNewsCardRecyclerViewAdapter(this)

        Timber.i("now OneNewsCardRecyclerViewAdapter should be created")

        articlesInRecyclerview = searchViewModel.recyclerViewArticles

        setHasOptionsMenu(true)

        binding.buttonSearch.setOnClickListener {
            Timber.i("button search clicked")
            this.context?.let { context ->
                val search = binding.searchText.text.toString()
                Timber.i("searched: $search")
                searchViewModel.searchInEverythingByKeyWords(context, search)
            }
        }

        binding.buttonFilter.setOnClickListener {
            val activity = this.requireActivity()
            activity.findNavController(this.id).navigate(R.id.action_searchFragment_to_filterFragment)
        }

        return binding.root
    }

    override fun onItemClicked(position: Int) {
        Toast.makeText(this.context, "clicked item $position", Toast.LENGTH_LONG).show()
        val clickedItem = articlesInRecyclerview.value?.get(position)
        Timber.i("onItemClicked: title: " + clickedItem?.title)
        userViewModel.currentArticle = articlesInRecyclerview.value?.get(position)
        val m = this.activity
        m?.findNavController(this.id)?.navigate(R.id.action_searchFragment_to_readFragment)
    }

    override fun onShareClicked(position: Int) {
        val articleUrl = articlesInRecyclerview.value?.get(position)?.url
        Timber.i("clicked button to share article $articleUrl")
        val shareIntent = ShareCompat.IntentBuilder.from(this.requireActivity())
            .setText(getString(R.string.share_text) + " " + articleUrl)
            .setType("text/plain")
            .intent
        try {
            startActivity(shareIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this.context, getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG).show()
        }
    }

    override suspend fun isOnFavorites(positionInRecyclerView: Int): Boolean {
        Timber.i("in checkOnFavorites")
        val user = User.get(userViewModel.currentUserId)
        if (context == null) Timber.i("context in checkOnFavorites is null")
        var answer = false
        val job = userViewModel.viewModelScope.launch(Dispatchers.IO) {
            Timber.i("started a job in checkOnFavorites")
            context?.let {
                articlesInRecyclerview.value?.let { it1 ->
                    answer = userViewModel.doesUserHaveArticle(user, it1[positionInRecyclerView])
                }
            }
        }
        job.join()
        Timber.i("completed a job in checkOnFavorites")
        return answer
    }

    /**
     * [onFavoriteClicked] returns false if user had an article on position and it was deleted
     * and returns true if user hadn't and it was added
     */
    override fun onFavoriteClicked(position: Int) {
        Timber.i("button favorite clicked")
        val user = User.get(userViewModel.currentUserId)
        userViewModel.viewModelScope.launch(Dispatchers.IO) {
            Timber.i("started a job in inFavoriteClicked to check if article is on favorites")
            if (isOnFavorites(position)) {
                articlesInRecyclerview.value?.get(position)?.let {
                    userViewModel.deleteArticleFromUser(user, it)

                }
            } else {
                articlesInRecyclerview.value?.get(position)?.let {
                    userViewModel.addArticleToUser(user, it)
                }

            }
        }
    }


    override fun onCreateOptionsMenu(
        menu: Menu,
        inflater: MenuInflater
    ) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.
        onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}
package com.example.lightupthenews.holders

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lightupthenews.application.reses
import com.example.lightupthenews.application.userViewModel
import com.example.lightupthenews.databinding.OneNewsCardBinding
import com.example.lightupthenews.network.ArticleInfo
import com.example.lightupthenews.user.NOT_LOGGED
import com.example.lightupthenews.user.User
import timber.log.Timber

val imgStarOff = reses.getDrawable(android.R.drawable.btn_star_big_off)
val imgStarOn = reses.getDrawable(android.R.drawable.btn_star_big_on)

class OneNewsCardViewHolder(
    private var binding: OneNewsCardBinding,
    private val listener: OneNewsCardViewHolder.OnItemClickListener
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

    init {
        itemView.setOnClickListener(this)
        binding.buttonFavorites.setOnClickListener(this)
        binding.buttonShare.setOnClickListener(this)
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
        fun onShareClicked(position: Int)
        fun onFavoriteClicked(position: Int)
        suspend fun isOnFavorites(position: Int): Boolean
    }

    override fun onClick(p0: View?) {
        Timber.i("addOrLoginUser, currentUserId: ${userViewModel.currentUserId}")
        val currentPosition = adapterPosition
        Timber.i("view is $p0, current position: $currentPosition")
        if (currentPosition != RecyclerView.NO_POSITION) {
            Timber.i("iside if currentPosition != RecyclerView.NO_POSITION")
            Timber.i("listener is $listener")

            when(p0) {

                binding.buttonFavorites -> {
                    Timber.i("clicked favorite button")
                    if (userViewModel.currentUserId == NOT_LOGGED) {
                        Toast.makeText(
                            binding.root.context,
                            "You can't add article to favorite while you're not logged in",
                            Toast.LENGTH_LONG
                        ).show()
                        Timber.i("user tried to save an article being not logged in")
                    }
                    else listener.onFavoriteClicked(currentPosition)
                }

                binding.buttonShare -> {
                    Timber.i("clicked Share button")
                    listener.onShareClicked(currentPosition)
                }

                else -> listener.onItemClicked(currentPosition)
            }
        }
    }

    fun bind(articleInfo: ArticleInfo) {
        Timber.i("bind called")
        binding.article = articleInfo
        binding.executePendingBindings() // causes the update to execute immediately
    }

    fun bindStarButton(articleInfo: ArticleInfo) {
        if (userViewModel.currentUserId != NOT_LOGGED) {
            val user = User.get(userViewModel.currentUserId)
            val starButton = binding.buttonFavorites
            if (userViewModel.doesUserHaveArticle(user, articleInfo)) {
                starButton.setImageDrawable(imgStarOn)
            } else starButton.setImageDrawable(imgStarOff)
        }
    }

}
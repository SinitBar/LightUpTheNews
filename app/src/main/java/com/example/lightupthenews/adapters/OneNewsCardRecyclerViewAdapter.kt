package com.example.lightupthenews.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.lightupthenews.databinding.OneNewsCardBinding
import com.example.lightupthenews.holders.OneNewsCardViewHolder
import com.example.lightupthenews.network.ArticleInfo
import timber.log.Timber

class OneNewsCardRecyclerViewAdapter(
    private val listener: OneNewsCardViewHolder.OnItemClickListener
) : ListAdapter<ArticleInfo,
        OneNewsCardViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OneNewsCardViewHolder {
        Timber.i("created")
        return OneNewsCardViewHolder(
            OneNewsCardBinding.inflate(
                LayoutInflater.from(parent.context)
            ), listener
        )
    }

    override fun onBindViewHolder(
        holder: OneNewsCardViewHolder,
        position: Int
    ) {
        Timber.i("onBindViewHolder called")
        val articleInfo = getItem(position)
        holder.bind(articleInfo)
        holder.bindStarButton(articleInfo)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ArticleInfo>() {
        override fun areItemsTheSame(
            oldItem: ArticleInfo, newItem: ArticleInfo
        ): Boolean = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: ArticleInfo, newItem: ArticleInfo
        ): Boolean = oldItem.content == newItem.content
    }
}
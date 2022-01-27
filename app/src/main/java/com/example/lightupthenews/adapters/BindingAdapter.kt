package com.example.lightupthenews.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.lightupthenews.R
import com.example.lightupthenews.network.ArticleInfo
import timber.log.Timber

@BindingAdapter("imageUrl") // executes when a property of imageUrl gets set
fun bindImage(imgView: ImageView, imgUrl: String?) {
    Timber.i("bindImage called")
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(imgView)
    }
    if (imgUrl == null) {
        imgView.setImageResource(R.drawable.ic_broken_image)
        Timber.i("imgUrl was null")
    }
}

@BindingAdapter("articleUrl")
fun bindArticleUrl(textView: TextView, url: String?) {
    Timber.i("bindArticleUrl called")
    url?.let {
        textView.text = it.toUri().buildUpon().scheme("https").build().toString()
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<ArticleInfo>?) {
    Timber.i("bindRecyclerView called")
    val adapter = recyclerView.adapter as OneNewsCardRecyclerViewAdapter
    adapter.submitList(data)
}
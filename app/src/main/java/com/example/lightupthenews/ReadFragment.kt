package com.example.lightupthenews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lightupthenews.application.userViewModel
import com.example.lightupthenews.databinding.FragmentReadBinding
import timber.log.Timber

class ReadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("created")
        val binding = FragmentReadBinding.inflate(inflater)
        val webView = binding.webView
        userViewModel.currentArticle?.url?.let { webView.loadUrl(it) }
        setHasOptionsMenu(true)
        return binding.root
    }
}
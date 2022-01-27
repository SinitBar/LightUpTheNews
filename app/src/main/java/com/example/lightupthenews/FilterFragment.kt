package com.example.lightupthenews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.lightupthenews.databinding.FragmentFilterBinding
import com.example.lightupthenews.network.LANGAUGE

class FilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFilterBinding>(
            inflater, R.layout.fragment_filter, container, false
        )
        binding.radioGroupLanguage.setOnCheckedChangeListener { _, i -> LANGAUGE = when(i) {
            R.id.button_ar_language -> "ar"
            R.id.button_de_language -> "de"
            R.id.button_en_language -> "en"
            R.id.button_es_language -> "es"
            R.id.button_fr_language -> "fr"
            R.id.button_he_language -> "he"
            R.id.button_it_language -> "it"
            R.id.button_nl_language -> "nl"
            R.id.button_no_language -> "no"
            R.id.button_pt_language -> "pt"
            R.id.button_ru_language -> "ru"
            R.id.button_se_language -> "se"
            R.id.button_ud_language -> "ud"
            R.id.button_zh_language -> "zh"
            else -> "en"
        }}

        return binding.root
    }
}
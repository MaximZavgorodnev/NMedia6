package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentDisplayingImagesBinding
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.StringArg

@AndroidEntryPoint
class DisplayingImagesFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDisplayingImagesBinding.inflate(
            inflater,
            container,
            false
        )

        val url = arguments?.textArg
        val urlImage ="${BuildConfig.BASE_URL}/images/$url"
        Glide.with(this)
            .load(urlImage)
            .error(R.drawable.ic_avatar_loading_error_48)
            .placeholder(R.drawable.ic_baseline_cruelty_free_48)
            .timeout(10_000)
            .into(binding.imageView)

        return binding.root
    }
    companion object {
        var Bundle.textArg: String? by StringArg
        var Bundle.longArg: Long by LongArg
    }
}
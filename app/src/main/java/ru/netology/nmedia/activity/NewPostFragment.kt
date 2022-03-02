package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        viewModel.data.observe(viewLifecycleOwner) { state ->
            if (state.systemError) {
                if (container != null) {
                    goError(container)
                }
            }
        }

        arguments?.textArg
            ?.let(binding.edit::setText)

        binding.ok.setOnClickListener {
            viewModel.changeContent(binding.edit.text.toString())
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
//            if (viewModel.data.value?.systemError == true) {
//                if (container != null) {
//                    goError(container)
//                }
//            }
            viewModel.loadPosts()

            findNavController().navigateUp()
        }




        return binding.root
    }

    fun goError(view: View){
        val text = viewModel.notificationText
        val snack = Snackbar.make(
            view, text,
            Snackbar.LENGTH_INDEFINITE
        )
        snack.setAction("Повторить?", View.OnClickListener {
            viewModel.retry()
        })
        snack.show()

    }
}
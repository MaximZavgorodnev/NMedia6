package ru.netology.nmedia.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.*
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

//    fun loadPosts() {
//        thread {
//            // Начинаем загрузку
//            _data.postValue(FeedModel(loading = true))
//            try {
//                // Данные успешно получены
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: IOException) {
//                // Получена ошибка
//                FeedModel(error = true)
//            }.also(_data::postValue)
//        }
//    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(t: Throwable, erorrCode: Int) {
                if (erorrCode != 0 ){
                    when (erorrCode) {
                        in 500..599 -> {
                            val snack = Snackbar.make(
                                it,
                                "This is a simple Snackbar",
                                Snackbar.LENGTH_LONG
                            )
                            snack.setAction("DISMISS", View.OnClickListener {
                                // executed when DISMISS is clicked
                                System.out.println("Snackbar Set Action - OnClick.")
                            })
                            snack.show()
                        }
                        else -> return
                    }
                } else _data.postValue(FeedModel(error = true))

            }
        })
    }

    fun save() {
//        edited.value?.let {
//            thread {
//                repository.save(it)
//                _postCreated.postValue(Unit)
//            }
//        }
//        edited.value = empty
        val post = edited.value
        repository.saveAsync( post!!, object : PostRepository.SaveRemoveCallback {
            override fun onSuccess() {
                _postCreated.postValue(Unit)
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
        edited.value = empty

    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
//        thread {
//            val updated = repository.likeById(id)
//            val posts = _data.value?.posts.orEmpty().map{if (it.id == id) updated else it }
//            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//
//        }
        repository.likeByIdAsync(id, object : PostRepository.LikeCallback {
            override fun onSuccess(id: Long, post: Post) {
                val posts = _data.value?.posts.orEmpty().map{if (it.id == id) post else it }
            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun disLikeById(id: Long) {
//        thread {
//            val updated = repository.disLikeById(id)
//            val posts = _data.value?.posts.orEmpty().map{if (it.id == id) updated else it }
//            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//        }

        repository.disLikeByIdAsync(id, object : PostRepository.LikeCallback {
            override fun onSuccess(id: Long, post: Post) {
                val posts = _data.value?.posts.orEmpty().map{if (it.id == id) post else it }
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })

    }

    fun removeById(id: Long) {
//        thread {
//            // Оптимистичная модель
//            val old = _data.value?.posts.orEmpty()
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }
//                )
//            )
//            try {
//                repository.removeById(id)
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }

        val old = _data.value?.posts.orEmpty()
        repository.removeByIdAsync(id, object : PostRepository.SaveRemoveCallback {
            override fun onSuccess() {
                _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id })
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }
}

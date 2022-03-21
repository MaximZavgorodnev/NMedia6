package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    var lastAction: ActionType? = null
    var lastId = 0L

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
//        repository.getAllAsync(object : PostRepository.GetAllCallback {
//            override fun onSuccess(posts: List<Post>) {
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//            }
//
//            override fun onError(t: Throwable, errorCode: Int) {
//                if (errorCode != 0 ){
//                    when (errorCode) {
//                        in 500..599 -> {
//                            _data.postValue(FeedModel(systemError = true))
//                        }
//                        else -> return
//                    }
//                } else _data.postValue(FeedModel(error = true))
//
//            }
//        })
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        lastAction = ActionType.SAVE
        if (edited.value?.content != "") {
            edited.value?.let {
                _postCreated.value = Unit
                viewModelScope.launch {
                    try {
                        repository.save(it)
                        _dataState.value = FeedModelState()
                    } catch (e: Exception) {
                        _dataState.value = FeedModelState(error = true)
                    }
                }
            }
        }
        edited.value = empty

//        val post = edited.value
//        repository.saveAsync( post!!, object : PostRepository.SaveRemoveCallback {
//            override fun onSuccess() {
//                _postCreated.postValue(Unit)
//                edited.value = empty
//            }
//
//            override fun onError(t: Throwable, errorCode: Int) {
//                if (errorCode != 0 ){
//                    when (errorCode) {
//                        in 400..599 -> {
//                            _data.postValue(FeedModel(systemError = true))
//                        }
//                        else -> return
//                    }
//                } else _data.postValue(FeedModel(error = true))
//            }
//        })


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

        lastAction = ActionType.LIKE
        lastId = id
        viewModelScope.launch {
            try {
                repository.likeById(id)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }

//        thread {
//            val updated = repository.likeById(id)
//            val posts = _data.value?.posts.orEmpty().map{if (it.id == id) updated else it }
//            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//
//        }
//        repository.likeByIdAsync(id, object : PostRepository.LikeCallback {
//            override fun onSuccess(id: Long, post: Post) {
//                val posts = _data.value?.posts.orEmpty().map{if (it.id == id) post else it }
//            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//            }
//
//            override fun onError(t: Throwable, errorCode: Int) {
//                if (errorCode != 0 ){
//                    when (errorCode) {
//                        in 500..599 -> {
//                            _data.postValue(FeedModel(systemError = true))
//                            val posts = _data.value?.posts.orEmpty()
//                            _data.postValue(FeedModel(posts = posts, systemError = true))
//                        }
//                        else -> return
//                    }
//                }
//                else _data.postValue(FeedModel(error = true))
//            }
//        })
    }

    fun disLikeById(id: Long) {

        lastAction = ActionType.DISLIKE
        lastId = id
        viewModelScope.launch {
            try {
                repository.disLikeById(id)


            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    //        thread {
//            val updated = repository.disLikeById(id)
//            val posts = _data.value?.posts.orEmpty().map{if (it.id == id) updated else it }
//            _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//        }
//        repository.disLikeByIdAsync(id, object : PostRepository.LikeCallback {
//            override fun onSuccess(id: Long, post: Post) {
//                val posts = _data.value?.posts.orEmpty().map{if (it.id == id) post else it }
//                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
//            }
//
//            override fun onError(t: Throwable, errorCode: Int) {
//                if (errorCode != 0 ){
//                    when (errorCode) {
//                        in 500..599 -> {
//                            _data.postValue(FeedModel(systemError = true))
//                            val posts = _data.value?.posts.orEmpty()
//                            _data.postValue(FeedModel(posts = posts, systemError = true))
//
//
//                        }
//                        else -> return
//                    }
//                } else _data.postValue(FeedModel(error = true))
//            }
//        })

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

        lastAction = ActionType.REMOVE
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
//        lastId = id
//        repository.removeByIdAsync(id, object : PostRepository.SaveRemoveCallback {
//            override fun onSuccess() {
//                _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }, systemError = false)
//                )
//            }
//
//            override fun onError(t: Throwable, errorCode: Int) {
//                if (errorCode != 0 ){
//                    when (errorCode) {
//                        in 500..599 -> {
//                            _data.postValue(FeedModel(systemError = true))
//                            val posts = _data.value?.posts.orEmpty()
//                            _data.postValue(FeedModel(posts = posts, systemError = true))
//                        }
//                        else -> return
//                    }
//                } else _data.postValue(FeedModel(error = true))
//            }
//        })
    }

    fun retry(){
        when (lastAction){
            ActionType.LIKE -> retryLikeById()
            ActionType.DISLIKE -> retryDisLikeById()
            ActionType.SAVE -> refreshPosts()
            ActionType.REMOVE -> retryRemove()

        }
    }

    fun retryLikeById(){
        lastId.let{
            likeById(it)}
    }

    fun retryDisLikeById(){
        lastId.let{
            disLikeById(it)}
    }

    fun retryRemove(){
        lastId.let{
            removeById(it)
        }
    }



}

enum class ActionType{
    LIKE,
    DISLIKE,
    REMOVE,
    SAVE
}

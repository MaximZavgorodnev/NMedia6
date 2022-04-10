package ru.netology.nmedia.viewmodel


import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    authorId = 0,
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = null,
    read = false
)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
    ) : ViewModel() {
    val data: Flow<PagingData<Post>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            val cached = repository.data.cachedIn(viewModelScope)
            cached.map { pagingData ->
                pagingData.map {
                        it.copy(ownedByMe = it.authorId == myId)
                    }
                }
        }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
    var lastAction: ActionType? = null
    var lastId = 0L

    fun update() = viewModelScope.launch {
        try {
            repository.update()
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
    }

    fun removeById(id: Long) {
        lastAction = ActionType.REMOVE
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun retry(){
        when (lastAction){
            ActionType.LIKE -> retryLikeById()
            ActionType.DISLIKE -> retryDisLikeById()
//            ActionType.SAVE -> refreshPosts()
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

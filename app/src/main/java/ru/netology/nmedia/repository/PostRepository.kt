package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun disLikeById(id: Long)

//    fun getAllAsync(callback: GetAllCallback)
//    fun likeByIdAsync(id: Long, callback:LikeCallback)
//    fun disLikeByIdAsync(id: Long, callback:LikeCallback)
//    fun saveAsync(post: Post, callback: SaveRemoveCallback)
//    fun removeByIdAsync(id: Long, callback: SaveRemoveCallback)
//
//    interface GetAllCallback {
//        fun onSuccess(posts: List<Post>)
//        fun onError(t: Throwable, erorrCode: Int)
//    }
//
//    interface SaveRemoveCallback {
//        fun onSuccess()
//        fun onError(t: Throwable, erorrCode: Int)
//    }
//
//    interface LikeCallback {
//        fun onSuccess(id: Long, post: Post)
//        fun onError(t: Throwable, erorrCode: Int)
//    }


}

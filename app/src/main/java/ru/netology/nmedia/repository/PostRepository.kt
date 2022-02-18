package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
//    fun getAll(): List<Post>
//    fun likeById(id: Long): Post
//    fun disLikeById(id: Long): Post
//    fun save(post: Post)
//    fun removeById(id: Long)

    fun getAllAsync(callback: GetAllCallback)
    fun likeByIdAsync(id: Long, callback:LikeCallback)
    fun disLikeByIdAsync(id: Long, callback:LikeCallback)
    fun saveAsync(post: Post, callback: SaveRemoveCallback)
    fun removeByIdAsync(id: Long, callback: SaveRemoveCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }

    interface LikeCallback {
        fun onSuccess(id: Long, post: Post)
        fun onError(e: Exception)
    }

    interface SaveRemoveCallback {
        fun onSuccess()
        fun onError(e: Exception)
    }
}

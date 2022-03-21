package ru.netology.nmedia.repository

import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao): PostRepository {
    var nextId: Long = 0L
    private var again = true
    private val memoryPosts = mutableListOf<Post>()
    override val data = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            if (memoryPosts.isNotEmpty()){
                memoryPosts.forEach{post -> val response = PostsApi.service.save(post)
                    if (!response.isSuccessful) {
                        memoryPosts.add(post)
                    }
                    val body = response.body() ?: throw ApiError(response.code(), response.message())
                    dao.insert(PostEntity.fromDto(body))
                    memoryPosts.remove(post)
                }
            }
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            nextId = (body.size+1).toLong()
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            nextId = dao.insert(PostEntity.fromDto(post))
            val newPost = post.copy(id = nextId)
            memoryPosts.add(newPost)
            val response = PostsApi.service.save(newPost)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
            memoryPosts.clear()


        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun disLikeById(id: Long) {
        try {
            val response = PostsApi.service.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
//        val request: Request = Request.Builder()
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    val body = response.body?.string() ?: throw RuntimeException("body is null")
//                    try {
//                        callback.onSuccess(gson.fromJson(body, typeToken.type))
//                    } catch (e: Exception) {
//                        callback.onError(e)
//                    }
//                }
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError(e)
//                }
//            })

//        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
//            override fun onResponse(call: retrofit2.Call<List<Post>>, response: retrofit2.Response<List<Post>>) {
//                if (!response.isSuccessful) {
//                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
//                    return
//                }
//                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
//                val error = 0
//                callback.onError(t,error)
//            }
//        })
//    }

//    override fun saveAsync(post: Post, callback: PostRepository.SaveRemoveCallback) {
//        val request: Request = Request.Builder()
//            .post(gson.toJson(post).toRequestBody(jsonType))
//            .url("${BASE_URL}/api/slow/posts")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    callback.onSuccess()
//                }
//
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError(e)
//                }
//            })

//        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post> {
//            override fun onResponse(
//                call: retrofit2.Call<Post>,
//                response: retrofit2.Response<Post>
//            ) {
//                if (!response.isSuccessful) {
//                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
//                    return
//                }
//                callback.onSuccess()
//            }
//            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
//                val error = 0
//                callback.onError(t,error)
//            }
//        })
//    }

//    override fun removeByIdAsync(id: Long, callback: PostRepository.SaveRemoveCallback) {
//        val request: Request = Request.Builder()
//            .delete()
//            .url("${BASE_URL}/api/slow/posts/$id")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    callback.onSuccess()
//                }
//
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError(e)
//                }
//            })
//        PostsApi.retrofitService.removeById(id).enqueue(object : retrofit2.Callback<Unit> {
//            override fun onResponse(call: retrofit2.Call<Unit>, response: retrofit2.Response<Unit>) {
//                if (!response.isSuccessful) {
//                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
//                    return
//                }
//                callback.onSuccess()
//            }
//
//            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
//                val error = 9
//                callback.onError(t, error)
//            }

//            override fun onResponse(call: retrofit2.Call<List<Post>>, response: retrofit2.Response<List<Post>>) {
//                if (!response.isSuccessful) {
//                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
//                    return
//                }
//                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
//            }
//
//            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
//                val error = 0
//                callback.onError(t,error)
//            }



//
//        })
//
//    }

//    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
//        val request: Request = Request.Builder()
//            .post("".toRequestBody())
//            .url("${BASE_URL}/api/slow/posts/$id/likes")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    val post = response.body?.string().let {
//                        gson.fromJson(it, Post::class.java)
//                    }
//                    callback.onSuccess(id, post)
//                }
//
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError(e)
//                }
//            })
//        PostsApi.retrofitService.likeById(id).enqueue(object : retrofit2.Callback<Post> {
//            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>
//            ) {
//                if (!response.isSuccessful) {
//                    callback.onError(java.lang.RuntimeException(response.message()), response.code()
//                    )
//                    return
//                }
//                val post = response.body()
//                if (post != null) {
//                    callback.onSuccess(id, post)
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
//                val error = 0
//                callback.onError(t, error)
//            }
//        })
//    }

//    override fun disLikeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
//        PostsApi.retrofitService.dislikeById(id).enqueue(object : retrofit2.Callback<Post> {
//            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>
//            ) {
//                if (!response.isSuccessful) {
//                    callback.onError(java.lang.RuntimeException(response.message()), response.code()
//                    )
//                    return
//                }
//                val post = response.body()
//                if (post != null) {
//                    callback.onSuccess(id, post)
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
//                val error = 0
//                callback.onError(t, error)
//            }
//        })
//        val request: Request = Request.Builder()
//            .delete()
//            .url("${BASE_URL}/api/slow/posts/$id/likes")
//            .build()
//
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onResponse(call: Call, response: Response) {
//                    val post = response.body?.string().let {
//                        gson.fromJson(it, Post::class.java)
//                    }
//                    callback.onSuccess(id, post)
//                }
//
//
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.onError(e)
//                }
//            })


//    }

}

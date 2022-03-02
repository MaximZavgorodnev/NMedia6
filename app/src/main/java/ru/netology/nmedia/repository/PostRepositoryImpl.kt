package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .build()
//    private val gson = Gson()
//    private val typeToken = object : TypeToken<List<Post>>() {}
//
//    companion object {
//        private const val BASE_URL = "http://10.0.2.2:9999"
//        private val jsonType = "application/json".toMediaType()
//    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
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

        PostsApi.retrofitService.getAll().enqueue(object : retrofit2.Callback<List<Post>> {
            override fun onResponse(call: retrofit2.Call<List<Post>>, response: retrofit2.Response<List<Post>>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
                    return
                }
                callback.onSuccess(response.body() ?: throw java.lang.RuntimeException("body is null"))
            }

            override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                val error = 0
                callback.onError(t,error)
            }
        })
    }

    override fun saveAsync(post: Post, callback: PostRepository.SaveRemoveCallback) {
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

        PostsApi.retrofitService.save(post).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(
                call: retrofit2.Call<Post>,
                response: retrofit2.Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
                    return
                }
                callback.onSuccess()
            }
            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                val error = 0
                callback.onError(t,error)
            }
        })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.SaveRemoveCallback) {
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
        PostsApi.retrofitService.removeById(id).enqueue(object : retrofit2.Callback<Unit> {
            override fun onResponse(call: retrofit2.Call<Unit>, response: retrofit2.Response<Unit>) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()), response.code())
                    return
                }
                callback.onSuccess()
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                val error = 9
                callback.onError(t, error)
            }

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




        })

    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
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
        PostsApi.retrofitService.likeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()), response.code()
                    )
                    return
                }
                val post = response.body()
                if (post != null) {
                    callback.onSuccess(id, post)
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                val error = 0
                callback.onError(t, error)
            }
        })
    }

    override fun disLikeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
        PostsApi.retrofitService.dislikeById(id).enqueue(object : retrofit2.Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: retrofit2.Response<Post>
            ) {
                if (!response.isSuccessful) {
                    callback.onError(java.lang.RuntimeException(response.message()), response.code()
                    )
                    return
                }
                val post = response.body()
                if (post != null) {
                    callback.onSuccess(id, post)
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                val error = 0
                callback.onError(t, error)
            }
        })
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


    }
}

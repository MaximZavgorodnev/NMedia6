package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.*
import okhttp3.*
import kotlinx.coroutines.flow.*
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao): PostRepository {
    var nextId: Long = 0L
    private val memoryPosts = mutableListOf<Post>()
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto).asFlow()
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            val daoSize = isSize()
            if (memoryPosts.isNotEmpty()) {
                coroutineScope {
                    memoryPosts.map { post ->
                        async {
                            val response = PostsApi.service.save(post)
                            if (!response.isSuccessful) {
                                memoryPosts.add(post)
                            }
                            memoryPosts.remove(post)
                            val body =
                                response.body() ?: throw ApiError(
                                    response.code(),
                                    response.message()
                                )
                            dao.insert(PostEntity.fromDto(body))
                        }
                    }.awaitAll()
                }
            }
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            body.filter { post ->
                post.id > daoSize
            }
            dao.insert(body.toEntity())
            if (daoSize < 6L) {
                dao.update()
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            val daoSize = isSize()
            delay(5_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            body.filter { post ->
                post.id > daoSize
            }
            dao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

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

    override suspend fun update(){
        dao.update()
    }

    override suspend fun isSize(): Long {
        return dao.isSize().toLong()
    }

}

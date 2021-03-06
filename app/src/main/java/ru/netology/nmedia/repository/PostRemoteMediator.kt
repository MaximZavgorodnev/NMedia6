package ru.netology.nmedia.repository

import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val db: AppDb

    ): RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (postDao.isEmpty()){
                        apiService.getLatest(state.config.pageSize)
                    } else {
                        val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(
                            endOfPaginationReached = false
                         )
                        apiService.getAfter(id, state.config.pageSize)
                    }
                }
                LoadType.PREPEND -> null
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    apiService.getBefore(id, state.config.pageSize)
                }
            }
            if (response != null) {
                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(
                    response.code(),
                    response.message(),
                )

                db.withTransaction {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            if (postRemoteKeyDao.isEmpty()) {
                                postRemoteKeyDao.insert(
                                    listOf(
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.AFTER,
                                            id = body.first().id,
                                        ),
                                        PostRemoteKeyEntity(
                                            type = PostRemoteKeyEntity.KeyType.BEFORE,
                                            id = body.last().id,
                                        ),
                                    )
                                )
                            } else {
                                postRemoteKeyDao.insert(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    )
                                )
                            }
                        }
                        LoadType.PREPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    id = body.first().id,
                                )
                            )
                        }
                        LoadType.APPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    id = body.last().id,
                                )
                            )
                        }
                    }
                    postDao.insert(body.toEntity())
                }
                return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
            } else return MediatorResult.Success(endOfPaginationReached = false)
        }
        catch (e: Exception){
            return MediatorResult.Error(e)
        }
    }


}
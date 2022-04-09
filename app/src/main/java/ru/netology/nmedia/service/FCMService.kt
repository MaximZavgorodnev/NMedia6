package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val recipientId = "recipientId"
    private val gson = Gson()
    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val id = message.data[recipientId]?.toLong()
        val idUser = appAuth.authStateFlow.value.id
        if (id == idUser || id == null) {
            message.data[action]?.let {
                val contains: Boolean =
                    try {
                        Action.valueOf(it)
                        true
                    } catch (e: IllegalArgumentException) {
                        false
                    }
                if (contains) {
                    when (Action.valueOf(it)) {
                        Action.LIKE -> handleLike(
                            Gson().fromJson(
                                message.data[content],
                                Like::class.java
                            )
                        )
                        Action.POST -> handlePost(
                            Gson().fromJson(
                                message.data[content],
                                NotificPost::class.java
                            )
                        )
                    }
                }
            }
        } else {
            appAuth.sendPushToken()
        }
    }

    override fun onNewToken(token: String) {
        println(token)
        appAuth.sendPushToken(token)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handlePost(post: NotificPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.posts_avatars_foreground)
            .setContentTitle(getString(R.string.notification_user_published_post, post.userName))
            .setContentText(getString(R.string.post_content, post.content))
            .setStyle(NotificationCompat.BigTextStyle())
            .build()
        NotificationManagerCompat.from(this).notify(Random.nextInt(100_00), notification)

    }
}

enum class Action{
    LIKE, POST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NotificPost(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val content: String
)


package pe.edu.upeu

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.atomic.AtomicInteger

private const val CHANNEL_ID = "pedidos"
private const val CHANNEL_NAME = "Pedidos"
private val nextNotificationId = AtomicInteger(1000)

object OrderNotifierHost {
    private var activity: Activity? = null
    private var requestPermission: ((onResult: (Boolean) -> Unit) -> Unit)? = null

    fun bind(activity: Activity, requestPermission: (onResult: (Boolean) -> Unit) -> Unit) {
        this.activity = activity
        this.requestPermission = requestPermission
    }

    fun unbind(activity: Activity) {
        if (this.activity === activity) {
            this.activity = null
            this.requestPermission = null
        }
    }

    fun show(title: String, body: String) {
        val current = activity ?: return
        if (needsPermission(current)) {
            requestPermission?.invoke { granted ->
                if (granted) post(current, title, body)
            }
            return
        }
        post(current, title, body)
    }
}

actual fun showOrderNotification(title: String, body: String) {
    OrderNotifierHost.show(title, body)
}

private fun needsPermission(activity: Activity): Boolean {
    return Build.VERSION.SDK_INT >= 33 &&
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
}

private fun post(activity: Activity, title: String, body: String) {
    val manager = NotificationManagerCompat.from(activity)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        activity.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(activity, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_notify_chat)
        .setContentTitle(title)
        .setContentText(body)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    if (!manager.areNotificationsEnabled()) return
    manager.notify(nextNotificationId.incrementAndGet(), notification)
}

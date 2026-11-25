package pe.edu.upeu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import pe.edu.upeu.data.session.SessionStoreHost

class MainActivity : ComponentActivity() {
    private var permissionCallback: ((Boolean) -> Unit)? = null

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val callback = permissionCallback
            permissionCallback = null
            callback?.invoke(granted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        SessionStoreHost.bind(applicationContext)

        OrderNotifierHost.bind(this) { onResult ->
            if (Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                onResult(true)
                return@bind
            }
            permissionCallback = onResult
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            App()
        }
    }

    override fun onDestroy() {
        OrderNotifierHost.unbind(this)
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
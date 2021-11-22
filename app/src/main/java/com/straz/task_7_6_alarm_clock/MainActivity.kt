package com.straz.task_7_6_alarm_clock

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.straz.task_7_6_alarm_clock.`object`.initApp
import com.straz.task_7_6_alarm_clock.`object`.mediaPlayer


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )

        window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        initApp(this)
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()
    }

    override fun supportNavigateUpTo(upIntent: Intent) {
        findNavController(R.id.fragmentHome).navigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
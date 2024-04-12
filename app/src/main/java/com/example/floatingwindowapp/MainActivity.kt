package com.example.floatingwindowapp

import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    private lateinit var btnMin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnMin = findViewById(R.id.btnMin)

        if(isServiceRunning()){
            stopService(Intent(this@MainActivity,FloatingWindowApp::class.java))
        }

        btnMin.setOnClickListener{
            if(checkOverlayPermission()){
                startService(Intent(this@MainActivity,FloatingWindowApp::class.java))
                finish()
            }else{
                requestFloatingWindowPermission()
            }
        }

    }

    private fun isServiceRunning():Boolean{
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for(service in manager.getRunningServices(Int.MAX_VALUE)){
            if(FloatingWindowApp::class.java.name == service.service.className){
                return true
            }
        }
        return false
    }

    private fun requestFloatingWindowPermission(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setTitle("Screen Overlay Permission Needed")
        builder.setMessage("Enable 'Display over the App' from settings")
        builder.setPositiveButton("Open Settings", DialogInterface.OnClickListener{dialog,whitch->
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent,RESULT_OK)
        })
        dialog = builder.create()
        dialog.show()
    }

    private fun checkOverlayPermission():Boolean{
        return if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            Settings.canDrawOverlays(this)
        }
        else return true
    }
}
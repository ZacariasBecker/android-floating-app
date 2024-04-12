package com.example.floatingwindowapp

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

class FloatingWindowApp : Service(){

    private lateinit var floatView: ViewGroup
    private lateinit var floatWindowLayoutParams: WindowManager.LayoutParams
    private var LAYOUT_TYPE: Int? = null
    private lateinit var windowManager: WindowManager
    private lateinit var btnMax: Button

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        floatView = inflater.inflate(R.layout.floating_layout,null) as ViewGroup

        btnMax = floatView.findViewById(R.id.btnMax)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else{
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST
        }

        floatWindowLayoutParams = WindowManager.LayoutParams(
            (500).toInt(),
            (500).toInt(),
            LAYOUT_TYPE!!,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParams.gravity = Gravity.CENTER
        floatWindowLayoutParams.x = 0
        floatWindowLayoutParams.y = 0

        windowManager.addView(floatView,floatWindowLayoutParams)

        btnMax.setOnClickListener{
            stopSelf()
            windowManager.removeView(floatView)

            val back = Intent(this@FloatingWindowApp, MainActivity::class.java)
            back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(back)
        }

        floatView.setOnTouchListener(object :View.OnTouchListener{

            val updatedFloatWindowLayoutParam = floatWindowLayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                when(event!!.action){

                    MotionEvent.ACTION_DOWN -> {
                        x = updatedFloatWindowLayoutParam.x.toDouble()
                        y = updatedFloatWindowLayoutParam.y.toDouble()

                        px = event.rawX.toDouble();
                        py = event.rawY.toDouble();
                    }

                    MotionEvent.ACTION_MOVE -> {
                        updatedFloatWindowLayoutParam.x = (x + event.rawX - px).toInt()
                        updatedFloatWindowLayoutParam.y = (y + event.rawY - py).toInt()

                        windowManager.updateViewLayout(floatView,updatedFloatWindowLayoutParam)
                    }
                }
                return false
            }
        })
    }
    override fun onDestroy(){
        super.onDestroy()
        stopSelf()
        windowManager.removeView(floatView)
    }
}
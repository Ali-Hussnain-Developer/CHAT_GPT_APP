package com.example.chatgpt.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatgpt.R

class SplashActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val window = this.window
        window.statusBarColor = Color.parseColor(getString(R.color.black))
        splash()
    }
    private fun splash() {
        val td: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                } finally {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    finish()
                }
            }
        }
        td.start()
    }
}

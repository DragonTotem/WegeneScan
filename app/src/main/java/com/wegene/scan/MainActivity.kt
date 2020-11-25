package com.wegene.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_click).setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(this@MainActivity, permissions, 1000)
            } else {
                val i = Intent(this@MainActivity, ScanCaptureActivity::class.java)
                startActivityForResult(i, 100)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val i = Intent(this@MainActivity, ScanCaptureActivity::class.java)
            startActivityForResult(i, 1000)
        } else {
            Toast.makeText(this, "权限申请失败", Toast.LENGTH_LONG).show()
        }
    }
}
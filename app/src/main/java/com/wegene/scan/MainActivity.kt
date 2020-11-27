package com.wegene.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.zxing.BaseCaptureActivity

class MainActivity : AppCompatActivity() {
    private lateinit var clickTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clickTv = findViewById<TextView>(R.id.tv_click)
        clickTv.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(Manifest.permission.CAMERA)
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissions,
                    BaseCaptureActivity.REQUEST_CAMERA_PERMISSION
                )
            } else {
                val i = Intent(this@MainActivity, ScanCaptureActivity::class.java)
                startActivityForResult(i, BaseCaptureActivity.REQUEST_SCAN_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BaseCaptureActivity.REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            val i = Intent(this@MainActivity, ScanCaptureActivity::class.java)
            startActivityForResult(i, BaseCaptureActivity.REQUEST_SCAN_CODE)
        } else {
            Toast.makeText(this, "权限申请失败", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BaseCaptureActivity.REQUEST_SCAN_CODE) {
            if (resultCode == RESULT_OK) {
                Log.w("MainActivity", "onActivityResult code: ${data?.extras?.getString("code")}")
                clickTv.text = data?.extras?.getString("code")
            }
        }
    }
}
package com.minangdev.myta.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.minangdev.myta.R
import kotlinx.android.synthetic.main.actionbar_onlyback.*

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        btn_appbar_back.setOnClickListener{
            onBackPressed()
        }
    }
}
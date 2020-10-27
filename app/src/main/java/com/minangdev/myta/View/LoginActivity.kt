package com.minangdev.myta.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.minangdev.myta.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btn_login.setOnClickListener(this)
    }

    fun moveActifity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.btn_login -> {
                submitLogin()
            }
        }
    }

    private fun submitLogin() {
        Toast.makeText(this, "login berhasil", Toast.LENGTH_SHORT).show()
        moveActifity()
    }

}
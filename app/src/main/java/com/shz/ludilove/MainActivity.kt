package com.shz.ludilove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userLogin:EditText = findViewById(R.id.userLogin)
        val userEmail:EditText = findViewById(R.id.userEmail)
        val userPass:EditText = findViewById(R.id.userPass)
        val regButton: Button = findViewById(R.id.buttonReg)
        var linkToAuth: TextView = findViewById(R.id.linkToAuth)
        linkToAuth.setOnClickListener{
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }


        regButton.setOnClickListener{
            var login:String = userLogin.text.toString().trim()
            var email:String = userEmail.text.toString().trim()
            var pass:String = userPass.text.toString().trim()
            if(login == "" || email == "" || pass == "")
            {
                Toast.makeText(this,"Заполните все поля!", Toast.LENGTH_LONG).show()
            }
            else {
                val User = User(login, email, pass)
                val db = DbHelper(this, null)
                db.addUser(User)
                Toast.makeText(this,"Пользователь добавлен!", Toast.LENGTH_LONG)
                userLogin.text.clear()
                userEmail.text.clear()
                userPass.text.clear()
            }
        }
    }
}
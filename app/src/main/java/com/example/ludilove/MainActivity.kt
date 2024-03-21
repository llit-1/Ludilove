package com.example.ludilove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        FullScreenHelper.enableFullScreen(window)

        val userLogin : EditText = findViewById(R.id.user_login)
        val userEmail : EditText = findViewById(R.id.user_email)
        val userPass: EditText = findViewById(R.id.user_pass)
        val button : Button = findViewById(R.id.button_reg)
        val linkToAuth : TextView = findViewById(R.id.link_to_auth)

        button.setOnClickListener {
            val login = userLogin.text.toString().trim();
            val email = userEmail.text.toString().trim();
            val pass = userPass.text.toString().trim();

            if(login == "" || email == "" || pass == "") {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                val db = DbHelper(this, null)
                val url = "https://appapi.ludilove.ru/api/auth"
                val queue = Volley.newRequestQueue(this)

                // Создайте объект JSON с данными, которые вы хотите отправить
                val jsonObject = JSONObject().apply {
                    put("name", login)
                    put("password", pass)
                }

                val request = object : JsonObjectRequest(
                    Method.POST,
                    url,
                    jsonObject,
                    Response.Listener<JSONObject> { response ->
                        println("Response: $response")
                        // Здесь вы можете добавить дополнительную обработку успешного ответа
                    },
                    Response.ErrorListener { error ->
                        println("Error: ${error.networkResponse}")
                        // Выведите содержимое ошибки для дальнейшей диагностики
                        error.printStackTrace()
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json"
                    }
                }

                queue.add(request)





                val user : User = User(1, login, email, pass, 1)
                db.clearCurrentUserTable()
                db.put_user(user)
                Toast.makeText(this, "Пользователь добавлен", Toast.LENGTH_LONG).show()
                linkToAuth.callOnClick()
                userLogin.text.clear()
                userEmail.text.clear()
                userPass.text.clear()
            }
        }

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}
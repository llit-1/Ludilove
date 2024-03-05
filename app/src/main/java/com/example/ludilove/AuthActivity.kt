package com.example.ludilove

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class AuthActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        FullScreenHelper.enableFullScreen(window)

        val loader: ProgressBar = findViewById(R.id.bars)
        loader.visibility = View.VISIBLE;
        val userLoginAuth : EditText = findViewById(R.id.user_login_auth)
        val userPassAuth: EditText = findViewById(R.id.user_pass_auth)
        val buttonAuth: Button = findViewById(R.id.button_auth)
        val linkToReg : TextView = findViewById(R.id.link_to_reg)

        // Проверяем наличие записанного в файловую бд пользователя
        // Если запись есть, тогда проверяем авторизацию, иначе идем на полную авторизацию
        val db = DbHelper(this, null)
        val lastUser = db.get_last_user()
        println(lastUser?.login)
        if(lastUser != null)
        {
            // Если пользователь авторизован пускаем его на главный экран, иначе продолжаем авторизацию
            if(lastUser.isAuth == 1) {
                checkUser(lastUser.login, lastUser.password)
            } else {
                userLoginAuth.setText(lastUser.login);
                userPassAuth.setText(lastUser.password);
            }
        }



        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            loader.visibility = View.INVISIBLE;
        }

        buttonAuth.setOnClickListener {
            val login = userLoginAuth.text.toString().trim();
            val pass = userPassAuth.text.toString().trim();
            val email = "k"

            if(login == "" || pass == "") {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                val user : User = User(1, login, email, pass, 1)
                db.clearCurrentUserTable()
                db.put_user(user)
                checkUser(login, pass)
                Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkUser(login : String, password : String) {
        val db = DbHelper(this, null)
        val url = "https://appapi.ludilove.ru/api/auth/$login, $password"
        val queue = Volley.newRequestQueue(this)
        val request = object : StringRequest(
            Method.GET,
            url,
            { response ->
                if(response.toBoolean()) {
                    db.change_last_user(login, 1)
                    val intent = Intent(this, ItemsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    val loader: ProgressBar = findViewById(R.id.bars)
                    loader.visibility = View.INVISIBLE;
                    startActivity(intent)
                    finish()
                }
            },
            { error ->
                println(error)
            }) {}
        queue.add(request)
    }

    override fun onResume() {
        super.onResume()
        val loader: ProgressBar = findViewById(R.id.bars)
        loader.visibility = View.INVISIBLE;
    }
}
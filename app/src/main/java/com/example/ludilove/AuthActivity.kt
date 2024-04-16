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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.yandex.mapkit.MapKitFactory

class AuthActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверяем наличие записанного в файловую бд пользователя
        // Если запись есть, тогда проверяем авторизацию, иначе идем на полную авторизацию
        val db = DbHelper(this, null)
        val lastUser = db.get_last_user()
        MapKitFactory.setApiKey("16b79d7a-5cb7-4281-840c-c47e5b487c75");
        if(lastUser != null)
        {
            // Если пользователь авторизован пускаем его на главный экран, иначе продолжаем авторизацию
            if(lastUser.isAuth == 1) {
                val loc = db.getLocationsData()
                if(loc == null) {
                    val coordHandler = getBestBakeryByCoord(this)
                    coordHandler.getBakery(object : getBestBakeryByCoord.CoordCallback {
                        override fun onCoordReceived(locations: List<Location>) {
                            db.deleteAndInsertLocation(locations[0])
                            checkUser(lastUser.login, lastUser.password)
                            return
                        }
                        override fun onCoordFailed() {
                            TODO("Not yet implemented")
                        }
                    })
                } else {
                    println("Что-то есть :/")
                    checkUser(lastUser.login, lastUser.password)
                    return
                }
            } else {
                setContentView(R.layout.activity_auth)
                val userLoginAuth : EditText = findViewById(R.id.user_login_auth)
                val userPassAuth: EditText = findViewById(R.id.user_pass_auth)
                userLoginAuth.setText(lastUser.login);
                userPassAuth.setText(lastUser.password);
            }
        } else {
            val coordHandler = getBestBakeryByCoord(this)
            coordHandler.getBakery(object : getBestBakeryByCoord.CoordCallback {
                override fun onCoordReceived(locations: List<Location>) {
                    db.deleteAndInsertLocation(locations[0])
                    setContentView(R.layout.activity_auth)
                    val userLoginAuth : EditText = findViewById(R.id.user_login_auth)
                    val userPassAuth: EditText = findViewById(R.id.user_pass_auth)
                    val buttonAuth: Button = findViewById(R.id.button_auth)
                    val linkToReg : TextView = findViewById(R.id.link_to_reg)

                    linkToReg.setOnClickListener {
                        val intent = Intent(this@AuthActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }

                    buttonAuth.setOnClickListener {
                        val login = userLoginAuth.text.toString().trim();
                        val pass = userPassAuth.text.toString().trim();
                        val email = "k"

                        if(login == "" || pass == "") {
                            Toast.makeText(this@AuthActivity, "Не все поля заполнены", Toast.LENGTH_LONG).show()
                        } else {
                            val user : User = User(1, login, email, pass, 1)
                            db.clearCurrentUserTable()
                            db.put_user(user)
                            checkUser(login, pass)
                        }
                    }
                    return
                }
                override fun onCoordFailed() {
                    TODO("Not yet implemented")
                }
            })
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
                if(response != null) {
                    val coordHandler = getBestBakeryByCoord(this)
                    coordHandler.getBakery(object : getBestBakeryByCoord.CoordCallback {
                        override fun onCoordReceived(locations: List<Location>) {
                            db.deleteAndInsertLocation(locations[0])
                            db.change_last_user(login, 1, response)
                        }
                        override fun onCoordFailed() {
                            TODO("Not yet implemented")
                        }
                    })
                    db.change_last_user(login, 1, response)
                    val intent = Intent(this, ItemsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG).show()
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Пользователь $login не авторизован", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                println(error)
                Toast.makeText(this, "Ошибка, попробуйте позже", Toast.LENGTH_LONG).show()
            }) {}
        queue.add(request)
    }
    override fun onResume() {
        super.onResume()
    }
}
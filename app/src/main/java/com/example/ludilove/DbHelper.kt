package com.example.ludilove

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, "app", factory, 11) {
            override fun onCreate(db: SQLiteDatabase?) {
                val query2 = "CREATE TABLE cart (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, user_login TEXT, price INT, image TEXT, count INT)"
                db!!.execSQL(query2)
                val query3 = "CREATE TABLE current_user (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, email TEXT, password TEXT, isAuth INT)"
                db.execSQL(query3)
            }

            override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                db!!.execSQL("DROP TABLE IF EXISTS cart")
                db!!.execSQL("DROP TABLE IF EXISTS current_user")
                onCreate(db)
            }

            // Очищает таблицу с последним пользователем
            fun clearCurrentUserTable() {
        val db = this.writableDatabase
        db.delete("current_user", null, null)
        db.close()
    }

    //     е
    fun change_last_user(login : String, isAuth : Int) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("isAuth", isAuth)
        val whereClause = "login = ?" // Здесь используется параметр
        val whereArgs = arrayOf(login) // Здесь передается значение параметра
        db.update("current_user", contentValues, whereClause, whereArgs)
        db.close()
    }

    fun put_user(user : User) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("login", user.login)
        contentValues.put("email", user.email)
        contentValues.put("password", user.password)
        contentValues.put("isAuth", 1)
        db.insert("current_user" , null, contentValues)
    }

    @SuppressLint("Recycle", "Range")
    fun get_last_user() : User? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM current_user", null)
        var user : User? = null;

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val login = cursor.getString(cursor.getColumnIndexOrThrow("login"))
            val password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
            val isAuth = cursor.getInt(cursor.getColumnIndexOrThrow("isAuth"))

            user = User(id, login, email, password, isAuth)
        }
        cursor?.close() // Закрытие курсора

        return user // Возвращение объекта user или null
    }
    @SuppressLint("Range")
    fun addToCart(name: String, price: Int, userLogin: String, image: String, count: Int) {
        val db = this.writableDatabase

        // Проверяем, существует ли запись с таким именем товара и пользовательским логином
        val query = "SELECT count FROM cart WHERE name = ? AND user_login = ?"
        val cursor = db.rawQuery(query, arrayOf(name, userLogin))

        if (cursor.moveToFirst()) {
            // Если запись существует, увеличиваем значение count на 1
            val currentCount = cursor.getInt(cursor.getColumnIndex("count"))
            val newCount = currentCount + 1

            val values = ContentValues()
            values.put("count", newCount)

            db.update("cart", values, "name = ? AND user_login = ?", arrayOf(name, userLogin))
        } else {
            // Если запись не существует, добавляем новую запись
            val values = ContentValues()
            values.put("name", name)
            values.put("user_login", userLogin)
            values.put("price", price)
            values.put("image", image)
            values.put("count", count)

            db.insert("cart", null, values)
        }

        cursor.close()
        db.close()
    }
    @SuppressLint("Range")
    fun getCartInfo(userLogin: String): List<Cart> {
        val db = this.readableDatabase
        val query = "SELECT * FROM cart WHERE user_login = ?"
        val cursor = db.rawQuery(query, arrayOf(userLogin))
        val cartItemList = mutableListOf<Cart>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val price = cursor.getInt(cursor.getColumnIndex("price"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val count = cursor.getInt(cursor.getColumnIndex("count"))
            // Другие поля, которые могут быть в вашей таблице
            val cartItem = Cart(id, name, userLogin, price, image, count)
            cartItemList.add(cartItem)
        }
        cursor.close()
        return cartItemList
    }
    @SuppressLint("Recycle")
    fun getCartCount(userLogin: String?): String? {
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT COUNT(*) FROM cart WHERE user_login = '${userLogin}'", null)
        println(result)
        return if (result.moveToFirst()) {
            val count = result.getString(0)
            result.close()
            count
        } else {
            result.close()
            "Пусто"
        }
    }

    @SuppressLint("Range")
    fun getCartItemQuantity(userLogin: String?, itemName: String): Int {
        val db = this.readableDatabase
        val query = "SELECT count FROM cart WHERE user_login = ? AND name = ?"
        val cursor = db.rawQuery(query, arrayOf(userLogin, itemName))
        var quantity = 0
        cursor.use {
            if (it.moveToFirst()) {
                quantity = it.getInt(it.getColumnIndex("count"))
            }
        }
        return quantity
    }

    fun deleteCartItemsByUserLogin(userLogin: String?) {
        val db = this.writableDatabase
        val query = "DELETE FROM cart WHERE user_login = '$userLogin'"
        db.execSQL(query)
    }
    fun deleteCartItem(userLogin: String, itemName: String) {
        val db = this.writableDatabase
        val query = "DELETE FROM cart WHERE user_login = '$userLogin' AND name = '$itemName'"
        db.execSQL(query)
    }
    @SuppressLint("Range")
    fun incrOrDecrItemsInCart(name: String, userLogin: String, action: String) {
        val db = this.writableDatabase

        // Проверяем, существует ли запись с таким именем товара и пользовательским логином
        val query = "SELECT count FROM cart WHERE name = ? AND user_login = ?"
        val cursor = db.rawQuery(query, arrayOf(name, userLogin))

        if (cursor.moveToFirst()) {
            // Если запись существует, увеличиваем значение count на 1
            val values = ContentValues()
            val currentCount = cursor.getInt(cursor.getColumnIndex("count"))
            if(action == "incr") {
                val newCount = currentCount + 1
                values.put("count", newCount)
            } else {
                val newCount = currentCount - 1
                values.put("count", newCount)

            }
            db.update("cart", values, "name = ? AND user_login = ?", arrayOf(name, userLogin))
        }
        cursor.close()
        db.close()
    }
}
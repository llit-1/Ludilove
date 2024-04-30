package com.example.ludilove

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
        SQLiteOpenHelper(context, "app", factory, 30) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query1 =
            "CREATE TABLE cart (id INTEGER, name TEXT, user_login TEXT, price INT, image TEXT, count INT)"
        db!!.execSQL(query1)
        val query2 =
            "CREATE TABLE current_user (id INTEGER PRIMARY KEY, login TEXT, email TEXT, password TEXT, isAuth INT)"
        db.execSQL(query2)
        val query3 = "CREATE TABLE json_data (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)"
        db.execSQL(query3)
        val query4 = "CREATE TABLE location (guid STRING PRIMARY KEY,name TEXT, rkcode INTEGER,latitude DOUBLE, longitude DOUBLE,distance DOUBLE,actual INTEGER)"
        db.execSQL(query4)

        val query5 = "CREATE TABLE allLocations (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)"
        db.execSQL(query5)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS cart")
        db!!.execSQL("DROP TABLE IF EXISTS current_user")
        db!!.execSQL("DROP TABLE IF EXISTS json_data")
        db!!.execSQL("DROP TABLE IF EXISTS location")
        db!!.execSQL("DROP TABLE IF EXISTS allLocations")
        onCreate(db)
    }

    // Очищает таблицу с последним пользователем
    fun clearCurrentUserTable() {
        val db = this.writableDatabase
        db.delete("current_user", null, null)
        db.close()
    }

    fun change_last_user(login: String, isAuth: Int, id: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("isAuth", isAuth)
        contentValues.put("id", id) // Добавляем поле id
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
    fun addToCart(id: Int, name: String, price: Int, userLogin: String, image: String, count: Int) {
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
            values.put("id", id)
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

    fun saveJsonData(jsonData: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("data", jsonData)
        }
        db.insert("json_data", null, values)
        db.close()
    }

    fun getJsonData(): String? {
        val db = this.readableDatabase
        val query = "SELECT data FROM json_data ORDER BY id DESC LIMIT 1"
        val cursor = db.rawQuery(query, null)
        var jsonData: String? = null
        cursor.use {
            if (it.moveToFirst()) {
                jsonData = it.getString(0)
            }
        }
        db.close()
        return jsonData
    }

    @SuppressLint("Recycle", "Range")
    fun getLocationsData() : Location? {
        val db = this.readableDatabase
        val query = "SELECT * FROM location LIMIT 1"
        val cursor = db.rawQuery(query, null)
        var loc: Location? = null // Инициализируем объект loc значением null

        if (cursor.moveToFirst()) {
            loc = Location(
                guid = cursor.getString(cursor.getColumnIndex("guid")),
                name = cursor.getString(cursor.getColumnIndex("name")),
                rkCode = cursor.getInt(cursor.getColumnIndex("rkcode")),
                latitude = cursor.getDouble(cursor.getColumnIndex("latitude")),
                longitude = cursor.getDouble(cursor.getColumnIndex("longitude")),
                distance = cursor.getDouble(cursor.getColumnIndex("distance")),
                actual = cursor.getInt(cursor.getColumnIndex("actual"))
            )
        }

        cursor.close()
        db.close()
        return loc
    }

    fun deleteAndInsertLocation(location: Location) {
        val db = this.writableDatabase

        // Удаляем все строки из таблицы location
        db.delete("location", null, null)
        // Добавляем новую строку
        val values = ContentValues().apply {
            put("guid", location.guid)
            put("name", location.name)
            put("rkcode", location.rkCode)
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("distance", location.distance)
            put("actual", location.actual)
        }

        db.insert("location", null, values)

        db.close()
    }

    fun getAllLocationsList() : String {
        val db = this.readableDatabase
        val query = "SELECT data FROM allLocations ORDER BY id DESC LIMIT 1"
        val cursor = db.rawQuery(query, null)
        var locationData: String = "";
        cursor.use {
            if (it.moveToFirst()) {
                locationData = it.getString(0)
            }
        }
        db.close()
        return locationData
    }

    fun putAllLocationsList(locString : String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("data", locString)
        }
        db.insert("allLocations", null, values)
        db.close()
    }

}
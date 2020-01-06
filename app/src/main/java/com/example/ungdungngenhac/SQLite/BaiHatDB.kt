package com.example.ungdungngenhac.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ungdungngenhac.models.BaiHat

class BaiHatDB(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSIOM) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE if not exists $TableName " +
                "($UserName TEXT,$LinkBaiHat TEXT, $TenBaiHat TEXT, $TenCaSi TEXT,$Avatar TEXT,PRIMARY KEY( $UserName,$LinkBaiHat))"
        p0?.execSQL(CREATE_TABLE)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

    fun addBaiHat(baihat: BaiHat,username:String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(UserName, username)
        values.put(LinkBaiHat, baihat.linkBaiHat)
        values.put(TenBaiHat, baihat.tenBaiHat)
        values.put(TenCaSi, baihat.tenCaSi)
        values.put(Avatar, baihat.anhBaiHat)
        db.insert(TableName, null, values)
        db.close()
    }

    fun addAll(arr: ArrayList<BaiHat>,username:String) {
        val db = this.writableDatabase
        for (baihat in arr) {
            val values = ContentValues()
            values.put(UserName, username)
            values.put(LinkBaiHat, baihat.linkBaiHat)
            values.put(TenBaiHat, baihat.tenBaiHat)
            values.put(TenCaSi, baihat.tenCaSi)
            values.put(Avatar, baihat.anhBaiHat)
            db.insert(TableName, null, values)
        }
        db.close()
    }

    fun getAllBaiHat(Username:String): ArrayList<BaiHat> {
        val baihats: ArrayList<BaiHat> = ArrayList()

        val sql = "Select *from $TableName where username='$Username'"
        val db = this.readableDatabase//chay cau truy van nay tra ve cursor
        val cursor = db.rawQuery(sql, null)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val baihat = BaiHat(cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(1))
                baihats.add(baihat)
            }
        }
        return baihats
    }

    fun deleteBaiHat(LinkBaiHat: String,Username: String) {
        val db = this.writableDatabase
        val sql = "Delete From $TableName Where linkbaihat='$LinkBaiHat' And username='$Username'"
        db.execSQL(sql)
        db.close()
    }


    companion object {
        private val DB_NAME = "dataBaiHat"
        private val DB_VERSIOM = 1;
        private val TableName = "newBaiHat"
        private val UserName = "username"
        private val LinkBaiHat = "linkbaihat"
        private val TenBaiHat = "tenbaihat"
        private val TenCaSi = "tencasi"
        private val Avatar = "avatar"
    }


}
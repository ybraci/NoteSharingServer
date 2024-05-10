package it.insubria.dbapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context):SQLiteOpenHelper(context, DATABASENAME, null, DATABASEVERTION) {
    companion object{ //cosi mettiamo le costanti qua al posto al di fuori della classe
        //somo inizializzate prima della creazione dell'oggetto
        private val DATABASENAME = "dbExample"
        private val DATABASEVERTION = 1
        private val TABLE_NAME = "UserTable"
        private val COLUMN_NAME_AGE = "age"
        private val COLUMN_ID = "id"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY,"+
                "$COLUMN_NAME_AGE INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //oldVersion quello che ha letto dal file che contiene la tabella
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertValue(value: String) {
        val db = this.writableDatabase
        val data = ContentValues()
        data.put(COLUMN_NAME_AGE, value)
        db.insert(TABLE_NAME, null, data)
        db.close()
    }

    fun getAllData(): ArrayList<String> {
        var arrList = ArrayList<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null) //null perchè non abbiamo argomenti
        if(cursor.moveToFirst()){ //ritorna true se esiste 1 primo elem
            do {
                val strValue = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_AGE))
                val intValue = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_AGE)) //lo stesso ma come int
                arrList.add("$strValue -> $intValue") //cosi vediamo la type affinity
            }while (cursor.moveToNext()) //finchè ci sono valori succ
        }
        db.close()
        return arrList
    }
}

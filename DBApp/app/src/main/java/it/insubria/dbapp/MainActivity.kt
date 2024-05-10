package it.insubria.dbapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val database = DatabaseHelper(this)
        database.insertValue("24seu")
        database.insertValue("54")

        val dataList = database.getAllData()
        var s = ""
        for (i in dataList){
            s = "$i \n"
        }
        findViewById<TextView>(R.id.tv1).text = "Valori str -> Valori int \n $s"
    }
}
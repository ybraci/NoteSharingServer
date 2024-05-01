package it.insubria.listapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.listView)
        val data = ArrayList<HashMap<String, Any>>()
        for (i in 1..100){
            val hm = HashMap<String, Any>()
            hm["Tittle"] = "Tittle $i"
            hm["Description"] = "This is the description nr $i"
            data.add(hm)
        }
        /*
        listView.adapter = SimpleAdapter(
            this,
            data,
            R.layout.listlayout,
            arrayOf("Tittle", "Description"),
            intArrayOf(R.id.textView, R.id.textView2)  //si chiaano cosi quelli di simple_list_item_2
        )
         */
        listView.adapter = MyCustomAdapter(
            this,
            data
        )
        listView.setOnItemClickListener{parent, view, position, id ->
            listView.setItemChecked(position, listView.isItemChecked(position))
        }
    }
}
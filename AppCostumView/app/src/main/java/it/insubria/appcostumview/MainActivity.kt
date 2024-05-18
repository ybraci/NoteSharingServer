package it.insubria.appcostumview

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var progr = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val btn = findViewById<Button>(R.id.btnStep)
        val pb = findViewById<CostumProgressBar>(R.id.progressBar)
        val tv = findViewById<TextView>(R.id.textView)
        btn.setOnClickListener{
            progr = if(progr<100) progr + 1 else 0
            pb.progress = progr
            tv.text = "${progr}/100"
        }
    }
}
package it.insubria.listapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class MyCustomAdapter(val context: Context,
    var data: ArrayList<HashMap<String, Any>>): BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position] //l'oggetto associato in quella riga
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // convertView sarà null se non ci sono view da riciclare ***********************
        //Quindi facciamo il riciclo delle view
        var newView = convertView
        if(convertView == null){
            //creiamo una nuova view con il nostro layout
            LayoutInflater.from(context).inflate(
                R.layout.listlayout, //che è il nostro
                parent,
                false
            )
        }

        val vtTittle = newView?.findViewById<TextView>(R.id.textView)
        val vtDesc = newView?.findViewById<TextView>(R.id.textView2)

        vtTittle?.text = data[position]["Tittle"].toString()
        vtDesc?.text = data[position]["Description"].toString()
        return newView!!
    }
}
package com.example.database

import java.sql.*

/*
 * Classe per effettuare la connessione al dbms. Si ha un unica istanza.
 */
object Database {
    //Variabili di login
    private var host: String? = null
    private var db_name: String? = null
    private var user: String? = null
    private var password: String? = null
    private val protocol = "jdbc:postgresql://"
    private var url: String? = null
    //Variabili di connessione
    private var database: Database? = null //è una istanza di questa classe
    private var connection: Connection? = null //per connettersi al DBMS
    private var statement: Statement? = null //l'oggetto usato per comunicare (querry, insert, ottenere risposte ...)

    @Throws(SQLException::class)
    fun getInstance(h: String, nomeDB: String, u: String, p: String) {
        if (database == null) {
            database = Database
        }
        this.host = h //per test localhost
        this.db_name = nomeDB
        this.user = u
        this.password = p
        this.url = "$protocol$host/$db_name"
        println(url)

        this.connection = DriverManager.getConnection(url, user, password)
        this.statement = (connection as Connection).createStatement( //Explicit cast di connection
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        )

    }
    /*
     * Metodo getter che restituisce la variabile di connection
     */
    fun getConnection(): Connection? {
        return connection
    }
}
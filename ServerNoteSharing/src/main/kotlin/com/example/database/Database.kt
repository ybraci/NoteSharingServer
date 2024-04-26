package com.example.database

import java.sql.*

object Database {
    //variabili di login
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

    /*
    init {
        this.host = host //per test localhost
        this.db_name = db_name
        this.user = user
        this.password = password
        this.url = "$protocol$host/$db_name"
        println(url) // da togliere

        this.connection = DriverManager.getConnection(url, user, password)
        this.statement = (connection as Connection).createStatement( //Explicit cast di connection
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        ) //quando viene usato per le query restituisce un oggetto che si chiama resultset. è una tabella che va attraversata


        //trammite un cursore. Allora bisogna stabilire come questo cursore si muova all'intrno di questa tabella: consente di procedere sia avanti che indietro + con
        // l'altro consente solo di leggere i risultati
    }

     */


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
        println(url) // da togliere

        this.connection = DriverManager.getConnection(url, user, password)
        this.statement = (connection as Connection).createStatement( //Explicit cast di connection
            ResultSet.TYPE_SCROLL_INSENSITIVE,
            ResultSet.CONCUR_READ_ONLY
        )

    }


    /**
     * Metodo getter che restituisce la variabile di classe *Statement*.
     *
     * @return La variabile di classe *Statement*.
     */
    fun getStatement(): Statement? {
        return statement
    }

    /**
     * Metodo getter che restituisce la variabile di classe *Connection*.
     *
     * @return La variabile di classe *Connection*.
     */
    fun getConnection(): Connection? {
        return connection
    }
}
package com.example

import com.example.database.Database
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.*

/*
 * Rappresenta il punto di partenza del server
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Quando il server parte effettua in automatico la connessione al dbms
    val database: Database = Database
    Database.getInstance("localhost", "db_noteSharing", "postgres", "postgres")

    configureSerialization()
    configureMonitoring()
    configureRouting(database)
}

package com.example

import com.example.database.Database
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.server.application.*


fun main(args: Array<String>) {
    /*
    print("Host db?: ")
    val host: String? = readLine()
    print("Nome db?: ")
    val nomedb: String? = readLine()
    print("Username db?: ")
    val user: String? = readLine()
    print("Password db?: ")
    val password: String? = readLine()
    val database: Database = Database
    Database.getInstance(host!!, nomedb!!, user!!, password!!)
    */

    io.ktor.server.netty.EngineMain.main(args)
    //embeddedServer(Netty, port = 8080) {module(database)}.start(wait = true)}
}

fun Application.module() {
    val database: Database = Database
    Database.getInstance("localhost", "db_noteSharing", "postgres", "postgres")

    configureSerialization()
    configureMonitoring()
    configureRouting(database)
}

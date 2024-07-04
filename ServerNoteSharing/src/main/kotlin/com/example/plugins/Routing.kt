package com.example.plugins

import com.example.database.Database
import com.example.routes.notesRoute
import com.example.routes.personeRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Configurazione delle route nel server. Qua viene passata anche l'istanza del db
fun Application.configureRouting(database: Database) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        // Aggiunta delle nostre route
        notesRoute(database)
        personeRoute(database) // Aggiungi questa riga per includere le route delle persone

    }
}

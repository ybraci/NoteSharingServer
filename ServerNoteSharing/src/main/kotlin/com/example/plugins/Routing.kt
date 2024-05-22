package com.example.plugins

import com.example.data.UserSession
import com.example.database.Database
import com.example.routes.notesRoute
import com.example.routes.personeRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureRouting(database: Database) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    // Le sessioni sono utilizzate per mantenere lo stato tra le richieste HTTP dello stesso utente.
    // Ad esempio, dopo che un utente si è autenticato con successo nel tuo sito web,
    // vuoi mantenere traccia di chi è quell'utente nelle richieste successive,
    // senza dover reinserire le credenziali ogni volta.
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    // UserSession è la classe che rappresenta le informazioni di sessione dell'utente.
    // "user_session" è il nome del cookie che verrà utilizzato per memorizzare queste informazioni.
    // impostiamo l'attributo SameSite del cookie su "lax" per migliorare la sicurezza.

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        // Static plugin. Try to access `/static/index.html`
        staticResources("/static", "static")

        notesRoute(database)
        personeRoute(database) // Aggiungi questa riga per includere le route delle persone
    }
}

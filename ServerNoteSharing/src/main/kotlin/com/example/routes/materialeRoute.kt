package com.example.routes

import com.example.data.MaterialeFisico
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.materialeRoute() {
    get("/materiale") {
        call.respond(
            HttpStatusCode.OK,
            MaterialeFisico(20, 1999, "Analisi matematica 1", "Appunti primo anno analisi matematica", "Varese", "Casorate Sempione", "Via Trieste 76", 21011
                )
        )
    }
}
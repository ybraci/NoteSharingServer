package com.example.routes

import com.example.comandiSQL.ComandiPersona
import com.example.data.Persona
import com.example.data.UserSession
import com.example.database.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.sql.Date
import java.sql.SQLException

fun Route.personeRoute(database: Database) {
    val comandiPersona = ComandiPersona(database)

    /*
    post("/UserLogin") {
        val request = call.receive<UserSession>()
        val authenticated = comandiPersona.loginUser(request.usernameSession, request.passwordSession)
        if (authenticated) {
            call.sessions.set(UserSession(request.usernameSession, request.passwordSession))
            call.respond(HttpStatusCode.OK, "Login successful")
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
    */

    post("/UserLogin") {
        val request = call.receive<UserSession>()
        val authenticated = comandiPersona.loginUser(request.usernameSession, request.passwordSession)
        if (authenticated) {
            call.sessions.set(UserSession(request.usernameSession, request.passwordSession))
            call.respond(HttpStatusCode.OK, mapOf("message" to "Login successful")) //da doc.: mapOf è serializable
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
        }
    }

    post("/UserSignUp") {
        val request = call.receive<Persona>()
        try {
            if (comandiPersona.isUsernameTaken(request.username)) {
                call.respond(HttpStatusCode.Conflict, "Username already in use")
            } else {
                comandiPersona.signUp(
                    username = request.username,
                    email = request.email,
                    password = request.password,
                    cf = request.cf,
                    nome = request.nome,
                    cognome = request.cognome,
                    provincia = request.provincia,
                    comune = request.comune,
                    via = request.via,
                    nrCivico = request.nrCivico,
                    cap = request.cap.toInt(),
                    dataN = Date.valueOf(request.dataN)
                )
                call.respond(HttpStatusCode.Created, "User ${request.username} registered successfully")
            }
        } catch (e: SQLException) {
            call.respond(HttpStatusCode.InternalServerError, "Error during registration: ${e.message}")
        }
    }


}
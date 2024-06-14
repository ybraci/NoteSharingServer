package com.example.routes

import com.example.comandiSQL.ComandiPersona
import com.example.data.Persona
import com.example.data.UserSession
import com.example.database.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.json.Json
import java.sql.Date
import java.sql.SQLException

fun Route.personeRoute(database: Database) {
    val comandiPersona = ComandiPersona(database)

    post("/UserLogin") {
        val request = call.receive<UserSession>()
        val authenticated = comandiPersona.loginUser(request.usernameSession, request.passwordSession)
        if (authenticated) {
            //call.sessions.set(UserSession(request.usernameSession, request.passwordSession))
            call.respond(HttpStatusCode.OK, mapOf("message" to "Login successful")) //da doc.: mapOf è serializable
        } else {
            call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
        }
    }

    post("/UserSignUp") {
        val request = call.receive<Persona>()
        try {
            if (comandiPersona.isUsernameTaken(request.username)) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to "Username already in use"))
            } else {
                comandiPersona.signUp(request)
                call.respond(HttpStatusCode.Created, mapOf("message" to "User registered successfully"))
            }
        } catch (e: SQLException) {
            println("SQLException during user signup: ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "Error during registration: ${e.message}"))
        } catch (e: Exception) {
            println("Exception during user signup: ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "Unexpected error during registration: ${e.message}"))
        }
    }


}
package com.example.routes

import com.example.comandiSQL.ComandiPersona
import com.example.data.CambioPasswordRequest
import com.example.data.MessageResponse
import com.example.data.Persona
import com.example.data.UserSession
import com.example.database.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.SQLException

// Contiene tutti i post e i get per i metodi relativi agli utenti

fun Route.personeRoute(database: Database) {
    val comandiPersona = ComandiPersona(database)

    // Metodo che permette al client di fare un tentativo di login
    post("/UserLogin") {
        val request = call.receive<UserSession>()
        try {
            val authenticated = comandiPersona.loginUser(request.usernameSession, request.passwordSession)
            if (authenticated) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Login successful")) //da doc.: mapOf è serializable
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    // Metodo che permette al client di fare la registrazione
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

    // Metodo che permette al client di modificare la password
    post("/CambioPassword"){
        val request = call.receive<CambioPasswordRequest>()
        try {
           comandiPersona.cambioP(request)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Password changed successfully"))
        } catch (e: SQLException) {
            println("SQLException during user signup: ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "Error during registration: ${e.message}"))
        } catch (e: Exception) {
            println("Exception during user signup: ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "Unexpected error during registration: ${e.message}"))
        }
    }

    get("/MailFromUsername"){
        val username = call.request.queryParameters["username"].toString()
        try{
            val email = ComandiPersona(database).getMail(username)
            call.respond(HttpStatusCode.OK, MessageResponse(email))
        }catch (e:NoSuchElementException) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
    }
}
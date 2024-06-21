package com.example.routes

import com.example.comandiSQL.*
import com.example.data.Annuncio
import com.example.data.MaterialeDigitale
import com.example.data.MaterialeFisico
import com.example.database.Database
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.notesRoute(database: Database) {
    post("/uploadPdf") {
        val multipart = call.receiveMultipart()
        var fileBytes: ByteArray? = null
        var fileName: String? = null
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    fileBytes = part.streamProvider().readBytes()
                    fileName = part.originalFileName
                }
                // Handle other form fields if needed
                else -> {
                    // Do nothing or handle other fields
                }
            }
        }
        // Save the file if needed
        fileBytes?.let {
            val file = File("C:/Users/david/Downloads/$fileName.pdf")
            file.writeBytes(it)
        }
        call.respond(HttpStatusCode.OK, "File uploaded successfully")
    }

    //prima di fare upload annuncio, bisogna inserire l'utente e che l'id della persona in ComandiAnnuncio.InsertAdv sia uguale alla mail di ComandiPersona.InsertUser
    post("/uploadAnnuncio"){
        val annuncio = call.receive<Annuncio>()
        call.respond(HttpStatusCode.OK, "Annuncio received successfully")
        ComandiAnnuncio(database).insertAdv(annuncio)
    }

    post("/uploadMD"){
        val mDigitale = call.receive<MaterialeDigitale>()
        call.respond(HttpStatusCode.OK, "Annuncio received successfully")
        ComandiMaterialeDigitale(database).insertMD(mDigitale)
    }

    post("/uploadMF"){
        val mFisico = call.receive<MaterialeFisico>()
        call.respond(HttpStatusCode.OK, "Annuncio received successfully")
        ComandiMaterialeFisico(database).insertMF(mFisico)
    }

    get("/getPDFs"){

    }

    post("/salvaAnnuncioComePreferito"){
        val idA = call.receive<String>().trim('"')
        //println("++++++++++++++++++++++++++++++++++++++++++++++++++++++ $idA")
        println("Hex representation: " + idA.toByteArray().joinToString("") { "%02x".format(it) })
        //aggiorno l'attributo preferito a true
        ComandiAnnuncio(database).updatePreferito(idA, true)
    }
    post("/eliminaAnnuncioComePreferito"){
        val idA = call.receive<String>().trim('"')
        //println("++++++++++++++++++++++++++++ $idA")
        //aggiorno l'attributo preferito a true
        ComandiAnnuncio(database).updatePreferito(idA, false)
    }
    post("/eliminaAnnuncio"){
        val idA = call.receive<String>()
        ComandiAnnuncio(database).eliminaAnnuncio(idA)
    }

    get("/listaAnnunci"){
        val listaA: ArrayList<Annuncio> = ComandiAnnuncio(database).getListaAnnunci()
        call.respond(HttpStatusCode.OK, listaA) // se non ci sono elementi invia la lista vuota
    }
    get("/myAnnunci"){
        val username = call.request.queryParameters["username"].toString()
        val listaA: ArrayList<Annuncio> = ComandiAnnuncio(database).getUsernameAnnunci(username)
        call.respond(HttpStatusCode.OK, listaA) // se non ci sono elementi invia la lista vuota
    }

    get("/listaAnnunciSalvati"){
        try {
            val username = call.request.queryParameters["username"].toString()
            val annunci: ArrayList<Annuncio> = ComandiAnnuncio(database).getAnnunciPreferiti(username)
            call.respond(HttpStatusCode.OK, annunci)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to retrieve saved announcements: ${e.message}")
            // Log the exception for further investigation
            application.log.error("Failed to retrieve saved announcements", e)
        }
    }

    get("/materialeFisicoAssociatoAnnuncio"){
        val idAnnuncio = call.request.queryParameters["idAnnuncio"].toString()

        if (idAnnuncio != null) {
            try {
                val materiale = ComandiMaterialeFisico(database).getMF(idAnnuncio)
                call.respond(HttpStatusCode.OK, materiale)
            } catch (e: NoSuchElementException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Materiale Fisico non esistente con id $idAnnuncio")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
            }
        } else {
            call.respond(HttpStatusCode.BadRequest, "Missing or invalid idAnnuncio parameter")
        }
    }

    get("/materialeDigitaleAssociatoAnnuncio"){
        val idAnnuncio = call.request.queryParameters["idAnnuncio"].toString()
        try {
            val materiale = ComandiMaterialeDigitale(database).getMD(idAnnuncio)
            call.respond(HttpStatusCode.OK, materiale)
        } catch (e: NoSuchElementException) {
            call.respond(HttpStatusCode.NotFound, e.message ?: "Materiale Digitale non esistente con id $idAnnuncio")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "An error occurred: ${e.message}")
        }

    }

}
package com.example.routes

import com.example.comandiSQL.ComandiAnnuncio
import com.example.comandiSQL.ComandiMaterialeDigitale
import com.example.comandiSQL.ComandiMaterialeFisico
import com.example.comandiSQL.ComandiPersona
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
        // ComandiPersona(database).InsertUser("pippo", "pippo", "pippo", "pippo", "pippo", "pippo", "pippo", "pippo", 20, 21011, java.sql.Date.valueOf("2024-05-11"))
        ComandiAnnuncio(database).InsertAdv(annuncio)
        //ComandiAnnuncio(database).InsertAdv(Annuncio("prova1esempioAnnuncio", "Esempio", "2024-04-12", "provaDescrizione", "pippo"))
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
    get("/listaAnnunci"){
        val listaA: ArrayList<Annuncio> = ComandiAnnuncio(database).getListaAnnunci()
        call.respond(HttpStatusCode.OK, listaA) // se non ci sono elementi invia la lista vuota
        //forse potrebbe essere utile salvarli localmente per motivi di efficienza
        //e poi aggiornarli in detterminati momenti ppure quando l'utente clicca il bottone
    }
    get("/materialeFisicoAssociatoAnnuncio"){
        val idAnnuncio = call.request.queryParameters["idAnnuncio"].toString()
        //val idAnnuncio = "47f539ab-2cb5-4665-83bc-7b7bd0ebcbea"
        if (idAnnuncio != null) {
            try {
                val materiale = ComandiMaterialeFisico(database).getMF(idAnnuncio)
                //val materiale = MaterialeFisico("47f539ab-2cb5-4665-83bc-7b7bd0ebcbea", 2, 2024, "algotitmi", "descriione: At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run on the proprietary Android version developed by Google", "Varese", "Varese", "Viale Aguggiari", 169, 21100)
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
        //val idAnnuncio = "47f539ab-2cb5-4665-83bc-7b7bd0ebcbea"
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
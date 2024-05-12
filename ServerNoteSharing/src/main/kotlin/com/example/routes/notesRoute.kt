package com.example.routes

import com.example.data.Annuncio
import com.example.data.MaterialeFisico
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.materialeRoute() {
    get("/materiale") {
        call.respond(
            HttpStatusCode.OK,
            MaterialeFisico(20, 1999, "Analisi matematica 1", "Appunti primo anno analisi matematica", "Varese", "Casorate Sempione", "Via Trieste 76", 21011
                )
        )
    }

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

    post("/uploadAnnuncio"){
        val annuncio = call.receive<Annuncio>()


        println("Received Annuncio***********************: ${annuncio.titolo} + materiale: ${annuncio.materialeD?.descrizioneMateriale}")

        call.respond(HttpStatusCode.OK, "Annuncio received successfully")
    }
}
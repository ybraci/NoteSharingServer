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
import java.sql.Date

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

}
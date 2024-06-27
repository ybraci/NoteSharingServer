package com.example.routes

import com.example.comandiSQL.*
import com.example.data.Annuncio
import com.example.data.DatoDigitale
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
        val datoDigitale = call.receive<DatoDigitale>()
        //Salvataggio nel db
        if (datoDigitale.idDato.isNotBlank()){ // se faccio datoDigitale|=null mi dice che sarà sempre true
            try{
                ComandiDatoDigitale(database).insertPDFtoDatoDigitale(datoDigitale)
                ComandiDatoDigitale(database).insertPDFtoHA(datoDigitale)
            }catch(e: Exception){
                call.respond(HttpStatusCode.InternalServerError)
                e.printStackTrace()
            }
            call.respond(HttpStatusCode.OK, mapOf("message" to "File uploaded successfully"))
        }else{
            call.respond(HttpStatusCode.NoContent, mapOf("message" to "File uploaded Unsuccessfully because the fileBytes or the name are missing"))
        }
    }

    get("/getPDFs"){
        val idAnnuncio = call.request.queryParameters["idAnnuncio"].toString()
        try{
            val pdf = ComandiDatoDigitale(database).getPDF(idAnnuncio)
            call.respond(HttpStatusCode.OK, pdf)
        }catch (e:NoSuchElementException) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
    }

    post("/uploadAnnuncio"){
        val annuncio = call.receive<Annuncio>()
        try {
            ComandiAnnuncio(database).insertAdv(annuncio)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
        call.respond(HttpStatusCode.OK, mapOf("message" to "Annuncio received successfully"))
    }

    post("/uploadMD"){
        val mDigitale = call.receive<MaterialeDigitale>()
        try {
            ComandiMaterialeDigitale(database).insertMD(mDigitale)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
        call.respond(HttpStatusCode.OK, mapOf("message" to "Annuncio received successfully"))
    }

    post("/uploadMF"){
        val mFisico = call.receive<MaterialeFisico>()
        try {
            ComandiMaterialeFisico(database).insertMF(mFisico)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
        call.respond(HttpStatusCode.OK, mapOf("message" to "Annuncio received successfully"))
    }

    post("/salvaAnnuncioComePreferito"){
        val idA = call.receive<String>().trim('"')
        //aggiorno l'attributo preferito a true
        try {
            ComandiAnnuncio(database).updatePreferito(idA, true)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Annuncio received successfully"))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
    }

    post("/eliminaAnnuncioComePreferito"){
        val idA = call.receive<String>().trim('"')
        //aggiorno l'attributo preferito a true
        try {
            ComandiAnnuncio(database).updatePreferito(idA, false)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Annuncio deleted successfully"))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
            e.printStackTrace()
        }
    }

    post("/eliminaAnnuncio"){
        try {
            val idA = call.receive<String>().trim('"')
            if(!ComandiAnnuncio(database).isFisico(idA)){
                //trovo i pdf associato (usando HA e ..)
                val listaPdfs = ComandiDatoDigitale(database).trovaPdfs(idA)
                //elimino i pdf
                for (pdf in listaPdfs){
                    ComandiDatoDigitale(database).eliminaPdf(pdf)
                }
            }
            //elimino annuncio
            ComandiAnnuncio(database).eliminaAnnuncio(idA)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Deleted successfully"))
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "InternalServerError"))
        }
    }

    get("/listaAnnunci"){
        val username = call.request.queryParameters["username"].toString()
        try {
            val listaA: ArrayList<Annuncio> = ComandiAnnuncio(database).getListaAnnunci(username)
            call.respond(HttpStatusCode.OK, listaA) // se non ci sono elementi invia la lista vuota
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    get("/myAnnunci"){
        val username = call.request.queryParameters["username"].toString()
        try {
            val listaA: ArrayList<Annuncio> = ComandiAnnuncio(database).getUsernameAnnunci(username)
            call.respond(HttpStatusCode.OK, listaA) // se non ci sono elementi invia la lista vuota
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    get("/listaAnnunciSalvati"){
        val username = call.request.queryParameters["username"].toString()
        try {
            val annunci: ArrayList<Annuncio> = ComandiAnnuncio(database).getAnnunciPreferiti(username)
            call.respond(HttpStatusCode.OK, annunci)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    get("/materialeFisicoAssociatoAnnuncio"){
        val idAnnuncio = call.request.queryParameters["idAnnuncio"].toString()
        if (idAnnuncio.isNotBlank()) {
            try {
                val materiale = ComandiMaterialeFisico(database).getMF(idAnnuncio)
                call.respond(HttpStatusCode.OK, materiale)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    get("/materialeDigitaleAssociatoAnnuncio"){
        val idAnnuncio = call.request.queryParameters["idAnnuncio"].toString()
        if (idAnnuncio.isNotBlank()) {
            try {
                val materiale = ComandiMaterialeDigitale(database).getMD(idAnnuncio)
                call.respond(HttpStatusCode.OK, materiale)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
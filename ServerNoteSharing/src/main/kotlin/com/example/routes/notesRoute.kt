package com.example.routes

import com.example.comandiSQL.*
import com.example.data.*
import com.example.database.Database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

// Contiene tutti i post e i get per i metodi relativi agli annunci
fun Route.notesRoute(database: Database) {
    // Metodo che permette al client di inviare un PDF
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

    // Metodo che permette al client di ricevere i PDF associati ad un annuncio
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

    // Metodo che permette al client di inviare un annuncio al server
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

    // Metodo che permette al client di inviare il contenuto di un annuncio di tipo digitale al server
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

    // Metodo che permette al client di inviare il contenuto di un annuncio di tipo fisico al server
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

    // Metodo che permette al client di eliminare un annuncio
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

    // Metodo che permette al client di ricevere tutti gli annunci tranne quelli pubblicati dall'utente stesso
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

    // Metodo che permette al client di ricevere tutti gli annunci creati da lui
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

    // Metodo che permette al client di ricevere i dati del materiale fisico dell'annuncio preso in input
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

    // Metodo che permette al client di ricevere i dati del materiale digitale dell'annuncio preso in input
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
package com.example.comandiSQL

import com.example.data.DatoDigitale
import com.example.database.Database
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

/*
 * Classe per effettuare operazioni sulle tabelle DatoDigitale e Ha del dbms
 */
class ComandiDatoDigitale(dbms: Database){
    private var database: Database = dbms

    // Metodo per inserire un nuovo pdf
    fun insertPDFtoDatoDigitale(datoD: DatoDigitale){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO DatoDigitale VALUES (?, ?, ?)")
                prepared?.apply {
                    setString(1, datoD.idDato)
                    setBytes(2, datoD.fileBytes)
                    setString(3, datoD.fileName)

                    executeUpdate()
                    close()
                }
            }
        } catch (e: SQLException) {
            database.getConnection()?.rollback()
            throw e
        } finally {
            database.getConnection()?.autoCommit = true
        }
    }

    // Metodo per inserire i dati nella tabella Ha. Nota: questa tabella fa il collegamento tra l'annuncio
    // e i pdf (perché lo stesso annuncio può avere più pdf)
    fun insertPDFtoHA(datoD: DatoDigitale){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO ha VALUES (?, ?)")
                prepared?.apply {
                    setString(1, datoD.idDato)
                    setString(2, datoD.idAnnuncio)

                    executeUpdate()
                    println("Execution successful. Closing PreparedStatement.")

                    close()
                }
                commit()
            }
        } catch (e: SQLException) {
            database.getConnection()?.rollback()
            throw e
        } finally {
            database.getConnection()?.autoCommit = true
        }
    }

    // Metodo che restituisce tutti i pdf dell'annuncio corrispondente all'id in input
    fun getPDF(idAnnuncio: String): ArrayList<DatoDigitale>{
        try {
            val query = ("SELECT IDdatoDigitale, idAnnuncio, dato, nome "
                    + "FROM ha, DatoDigitale "
                    + "WHERE id = IDdatoDigitale AND idAnnuncio = ? ;")
            val preparedStatement = database.getConnection()!!.prepareStatement(query)
            preparedStatement.apply {
                setString(1, idAnnuncio)
            }
            val result = preparedStatement.executeQuery()

            var lista: ArrayList<DatoDigitale> = ArrayList<DatoDigitale>()
            while (result.next()) {
                    lista.add(DatoDigitale(
                        result.getString("IDdatoDigitale"),
                        result.getString("idAnnuncio"),
                        result.getBytes("dato"),
                        result.getString("nome")
                    ))
            }
            result.close()
            preparedStatement.close()
            if(lista.isNotEmpty()){
                return lista
            }else{
                throw NoSuchElementException("File non esistente")
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    // Metodo che restiruisce gli id dei pdf (DatoDigitale) corrispondenti ad un Annuncio
    fun trovaPdfs(idAnnuncio: String): ArrayList<String>{
        try {
            val query = ("SELECT IDdatoDigitale "
                        + "FROM ha "
                        + "WHERE idAnnuncio = ? ;")
            val preparedStatement = database.getConnection()!!.prepareStatement(query)
            preparedStatement.apply {
                setString(1, idAnnuncio)
            }
            val result = preparedStatement.executeQuery()

            var lista: ArrayList<String> = ArrayList()
            while (result.next()) {
                lista.add(result.getString("IDdatoDigitale"))
            }
            result.close()
            preparedStatement.close()
            if(lista.isNotEmpty()){
                return lista
            }else{
                throw NoSuchElementException("File non esistente")
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    // Metodo che elimina un pdf
    fun eliminaPdf(idPdf: String) {
        val query = "DELETE FROM DatoDigitale WHERE id = ?;"
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = database.getConnection()
            if (connection == null || connection.isClosed) {
                throw SQLException("Failed to obtain a valid connection.")
            }
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, idPdf)
            val rowsAffected = preparedStatement.executeUpdate()
        } catch (e: SQLException) {
            throw e
        } finally {
            preparedStatement?.close()
            println("PreparedStatement closed")
        }
    }

}
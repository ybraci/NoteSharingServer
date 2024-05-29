package com.example.comandiSQL

import com.example.data.Annuncio
import com.example.data.DatoDigitale
import com.example.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException

class ComandiDatoDigitale(dbms: Database){
    private var database: Database = dbms

    fun insertPDF(fileBytes: ByteArray, fileName: String){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO DatoDigitale VALUES (?, ?, ?)")
                prepared?.apply {
                    setString(1, UUID.randomUUID().toString())
                    setBytes(2, fileBytes)
                    setString(3, fileName)

                    executeUpdate()
                    close() // Close the PreparedStatement
                }
                commit() // Commit the transaction
            }
        } catch (e: SQLException) {
            // Rollback the transaction in case of any exception
            database.getConnection()?.rollback()
            throw e
        } finally {
            // Set auto-commit back to true after the transaction is done
            database.getConnection()?.autoCommit = true
        }
    }
    fun getPDF(idAnnuncio: String): DatoDigitale{
        val query = ("SELECT id, datoDigitale, nome "
                + "FROM Annuncio, DatoDigitale"
                + "WHERE id = IDdatoDigitale AND idAnnuncio = ? ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        preparedStatement.apply {
            setString(1, idAnnuncio)
        }
        val result = preparedStatement.executeQuery()

        var datoD: DatoDigitale? = null
        while (result.next()) {
                datoD = DatoDigitale(
                    result.getString("id"),
                    result.getBytes("datoDigitale"),
                    result.getString("nome")
            )
        }
        result.close()
        preparedStatement.close()
        if(datoD!=null){
            return datoD
        }else{
            throw NoSuchElementException("File non esistente")
        }
    }

}
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
                    close() // Close the PreparedStatement
                }
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
    fun insertPDFtoHA(datoD: DatoDigitale){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO ha VALUES (?, ?)")
                prepared?.apply {
                    setString(1, datoD.idDato)
                    setString(2, datoD.idAnnuncio)

                    executeUpdate()
                    close() // Close the PreparedStatement
                }
                commit() // Commit the transaction
            }
        } catch (e: SQLException) {
            // Rollback the transaction in case of any exception
            database.getConnection()?.rollback()
            e.printStackTrace()
        } finally {
            // Set auto-commit back to true after the transaction is done
            database.getConnection()?.autoCommit = true
        }
    }
    fun getPDF(idAnnuncio: String): ArrayList<DatoDigitale>{
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
    }

}
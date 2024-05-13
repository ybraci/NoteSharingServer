package com.example.comandiSQL

import com.example.data.Annuncio
import com.example.database.Database
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat

class ComandiAnnuncio(dbms: Database){
    private var database: Database = dbms

    fun InsertAdv(annuncio: Annuncio){

        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO Annuncio VALUES (?,?,?,?,?)")
                prepared?.apply {
                    setString(1, annuncio.id)
                    setString(2, annuncio.titolo)
                    setDate(3, Date(SimpleDateFormat(String.toString()).parse(annuncio.data).time)) //"2024-05-13"
                    setString(4, annuncio.descrizioneAnnuncio)
                    setString(5, annuncio.idProprietario)

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

}
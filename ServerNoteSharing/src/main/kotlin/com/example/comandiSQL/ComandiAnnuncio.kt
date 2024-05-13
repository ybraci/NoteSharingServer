package com.example.comandiSQL

import com.example.database.Database
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException

class ComandiAnnuncio(dbms: Database){
    private var database: Database = dbms

    fun InsertAdv(
        id: String,
        titolo: String,
        data: Date,
        descrizioneAnnuncio: String,
        idProprietario: String,
    ){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO Annuncio VALUES (?,?,?,?,?)")
                prepared?.apply {
                    setString(1, id)
                    setString(2, titolo)
                    setDate(3, data)
                    setString(4, descrizioneAnnuncio)
                    setString(5, idProprietario)

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
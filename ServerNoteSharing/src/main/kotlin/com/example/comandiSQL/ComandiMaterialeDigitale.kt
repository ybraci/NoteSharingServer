package com.example.comandiSQL

import com.example.data.MaterialeDigitale
import com.example.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException

class ComandiMaterialeDigitale(dbms: Database){
    private var database: Database = dbms

    fun insertMD(mDigitale: MaterialeDigitale){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO MaterialeDigitale VALUES (?, ?, ?, ?)")
                prepared?.apply {
                    setString(1, mDigitale.id)
                    setInt(2, mDigitale.annoRiferimento)
                    setString(3, mDigitale.nomeCorso)
                    setString(4, mDigitale.descrizioneMateriale)

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
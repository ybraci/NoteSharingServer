package com.example.comandiSQL

import com.example.data.MaterialeFisico
import com.example.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException

class ComandiMaterialeFisico(dbms: Database) {
    private var database: Database = dbms
    fun insertMF(mFisico: MaterialeFisico){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO MaterialeFisico VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                prepared?.apply {
                    setString(1, mFisico.id)
                    setInt(2, mFisico.costo)
                    setInt(3, mFisico.annoRiferimento)
                    setString(4, mFisico.nomeCorso)
                    setString(5, mFisico.descrizioneMateriale)
                    setString(6, mFisico.provincia)
                    setString(7, mFisico.comune)
                    setString(8, mFisico.via)
                    setInt(9, mFisico.numeroCivico)
                    setInt(10, mFisico.cap)

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
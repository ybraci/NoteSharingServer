package com.example.comandiSQL

import com.example.data.MaterialeDigitale
import com.example.data.MaterialeFisico
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
                    setInt(3, mDigitale.areaMateriale)
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

    fun getMD(idAnnuncio: String): MaterialeDigitale {
        val query = ("SELECT * "
                + "FROM MaterialeDigitale "
                + "WHERE id = ? ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        preparedStatement.setString(1, idAnnuncio)
        val result = preparedStatement.executeQuery()

        var materialeD: MaterialeDigitale? = null
        while (result.next()) {
            materialeD = MaterialeDigitale(
                result.getString("id"),
                result.getInt("annoRif"),
                result.getInt("areaMateriale"),
                result.getString("descrizioneMateriale")
            )
        }
        result.close()
        preparedStatement.close()

        if(materialeD!=null){
            return materialeD
        }else{
            throw NoSuchElementException("Materiale Digitale non esistente con id $idAnnuncio")
        }

    }

}
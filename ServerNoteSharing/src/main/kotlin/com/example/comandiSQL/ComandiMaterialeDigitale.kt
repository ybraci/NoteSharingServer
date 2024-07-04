package com.example.comandiSQL

import com.example.data.MaterialeDigitale
import com.example.data.MaterialeFisico
import com.example.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException

/*
 * Classe per effettuare operazioni sulla tabella DatoDigitale del dbms
 */
class ComandiMaterialeDigitale(dbms: Database){
    private var database: Database = dbms

    // Metodo per inserire i dati del materiale digitale di un nuovo annuncio
    fun insertMD(mDigitale: MaterialeDigitale){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO MaterialeDigitale VALUES (?, ?, ?)")
                prepared?.apply {
                    setString(1, mDigitale.id)
                    setInt(2, mDigitale.annoRiferimento)
                    setString(3, mDigitale.descrizioneMateriale)

                    executeUpdate()
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

    // Metodo che restituisce i dati del materiale digitale di un annuncio con id corrispondente a quello in input
    fun getMD(idAnnuncio: String): MaterialeDigitale {
        try {
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
        } catch (e: SQLException) {
            throw e
        }

    }

}
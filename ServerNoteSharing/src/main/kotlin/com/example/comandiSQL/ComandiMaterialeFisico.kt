package com.example.comandiSQL

import com.example.data.MaterialeFisico
import com.example.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException

/*
 * Classe per effettuare operazioni sulla tabella MaterialeFisico del dbms
 */
class ComandiMaterialeFisico(dbms: Database) {
    private var database: Database = dbms

    // Metodo per inserire i dati del materiale fisico di un nuovo annuncio
    fun insertMF(mFisico: MaterialeFisico){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO MaterialeFisico VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                prepared?.apply {
                    setString(1, mFisico.id)
                    setInt(2, mFisico.costo)
                    setInt(3, mFisico.annoRiferimento)
                    setString(4, mFisico.descrizioneMateriale)
                    setString(5, mFisico.provincia)
                    setString(6, mFisico.comune)
                    setString(7, mFisico.via)
                    setInt(8, mFisico.numeroCivico)
                    setInt(9, mFisico.cap)

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

    // Metodo che restituisce i dati del materiale fisico di un annuncio con id corrispondente a quello in input
    fun getMF(idAnnuncio: String): MaterialeFisico {
        try {
            val query = ("SELECT * "
                    + "FROM MaterialeFisico "
                    + "WHERE id = ? ;")
            val preparedStatement = database.getConnection()!!.prepareStatement(query)
            preparedStatement.setString(1, idAnnuncio)
            val result = preparedStatement.executeQuery()

            var materialeF: MaterialeFisico? = null
            while (result.next()) {
                materialeF = MaterialeFisico(
                    result.getString("id"),
                    result.getInt("costo"),
                    result.getInt("annoRif"),
                    result.getString("descrizioneMateriale"),
                    result.getString("provincia"),
                    result.getString("comune"),
                    result.getString("via"),
                    result.getInt("nrCivico"),
                    result.getInt("cap")
                    )
            }
            result.close()
            preparedStatement.close()

            if(materialeF!=null){
                return materialeF
            }else{
                throw NoSuchElementException("Materiale Fisico non esistente con id $idAnnuncio")
            }
        } catch (e: SQLException) {
            throw e
        }

    }
}
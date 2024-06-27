package com.example.comandiSQL

import com.example.data.Annuncio
import com.example.database.Database
import java.sql.*
import java.text.SimpleDateFormat
import java.time.LocalDate

class ComandiAnnuncio(dbms: Database){
    private var database: Database = dbms

    fun insertAdv(annuncio: Annuncio){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO Annuncio VALUES (?,?,?,?,?,?,?,?)")
                prepared?.apply {
                    setString(1, annuncio.id)
                    setString(2, annuncio.titolo)
                    setDate(3, java.sql.Date(SimpleDateFormat("yyyy-MM-dd").parse(annuncio.data).time)) //"2024-05-13"
                    setString(4, annuncio.descrizioneAnnuncio)
                    setString(5, annuncio.idProprietario)
                    setBoolean(6, annuncio.tipoMateriale)
                    setInt(7, annuncio.areaAnnuncio)
                    setBoolean(8, annuncio.preferito)

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

    fun getListaAnnunci(username: String): ArrayList<Annuncio> {
        //Questa query seleziona tutti gli annunci tranne quelli che ha pubblicato l'utente loggato
        val query = ("SELECT * FROM Annuncio WHERE id NOT IN (SELECT id FROM Annuncio WHERE idProprietarioPersona = ?) ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        preparedStatement?.apply {
            setString(1, username)
        }
        val result = preparedStatement.executeQuery()
        val listaA: ArrayList<Annuncio> = ArrayList()
        while (result.next()) {
            listaA.add(Annuncio(result.getString("id"),
                result.getString("titolo"),
                result.getDate("data").toString(),
                result.getString("descrizioneAnnuncio"),
                result.getBoolean("tipoMateriale"),
                result.getString("idProprietarioPersona"),
                result.getInt("areaAnnuncio"),
                result.getBoolean("preferito")
                ))
        }
        result.close()
        preparedStatement.close()
        return listaA
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    fun getAnnunciPreferiti(username: String): ArrayList<Annuncio> {
        val listaA: ArrayList<Annuncio> = ArrayList()
        val query = ("SELECT * "
                + "FROM Annuncio "
                + "WHERE idProprietarioPersona = ? AND preferito = true ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        preparedStatement?.apply {
            setString(1, username)
        }
        val result = preparedStatement.executeQuery()
        while (result.next()) {
            listaA.add(Annuncio(result.getString("id"),
                result.getString("titolo"),
                result.getDate("data").toString(),
                result.getString("descrizioneAnnuncio"),
                result.getBoolean("tipoMateriale"),
                result.getString("idProprietarioPersona"),
                result.getInt("areaAnnuncio"),
                result.getBoolean("preferito")
            ))
        }
        result.close()
        preparedStatement.close()
        return listaA
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    //Solo questa versione non comporta la chiusura della connesione dal parte del db
    fun updatePreferito(idAnnuncio: String, preferito: Boolean) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = database.getConnection()
            if (connection == null || connection.isClosed) {
                throw SQLException("Failed to obtain a valid connection.")
            }
            connection.autoCommit = false
            val query = "UPDATE annuncio SET preferito = ? WHERE id = ?"
            println("Executing query: $query with preferito=$preferito and id=$idAnnuncio")
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setBoolean(1, preferito)
            preparedStatement.setString(2, idAnnuncio)
            val rowsUpdated = preparedStatement.executeUpdate() //*********************
            if (rowsUpdated > 0) {
                println("Successfully updated $rowsUpdated row(s).")
            } else {
                println("No rows were updated. Check if idAnnuncio=$idAnnuncio exists.")
            }
            connection.commit()
        } catch (e: SQLException) {
            connection?.rollback()
            throw e
        } finally {
            preparedStatement?.close()
            connection?.autoCommit = true
            // connection?.close()
        }
    }

    fun eliminaAnnuncio(idA: String) {
        val query = "DELETE FROM annuncio WHERE id = ?;"
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = database.getConnection()
            if (connection == null || connection.isClosed) {
                throw SQLException("Failed to obtain a valid connection.")
            }
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, idA)
            println("Set idA to PreparedStatement: $idA")
            val rowsAffected = preparedStatement.executeUpdate()
            println("Execute update completed. Rows affected: $rowsAffected")
        } catch (e: SQLException) {
            throw e
        } finally {
            preparedStatement?.close()
            println("PreparedStatement closed")
        }
    }

    //restituisce tutti gli annunci pubblicati dall'utente
    fun getUsernameAnnunci(username: String): ArrayList<Annuncio> {
        val query = ("SELECT * FROM Annuncio WHERE idProprietarioPersona = ?;")
        val listaA: ArrayList<Annuncio> = ArrayList()
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        preparedStatement?.apply {
            setString(1, username)
        }
        val result = preparedStatement.executeQuery()
        while (result.next()) {
            listaA.add(Annuncio(result.getString("id"),
                result.getString("titolo"),
                result.getDate("data").toString(),
                result.getString("descrizioneAnnuncio"),
                result.getBoolean("tipoMateriale"),
                result.getString("idProprietarioPersona"),
                result.getInt("areaAnnuncio"),
                result.getBoolean("preferito")
            ))
        }
        result.close()
        preparedStatement.close()
        return listaA
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    //Se restituisce true l'annuncio è fisico
    fun isFisico(idA: String): Boolean {
        val query = ("SELECT tipomateriale FROM annuncio WHERE id = ? ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        preparedStatement?.apply {
            setString(1, idA)
        }
        val result = preparedStatement.executeQuery()
        var resBool: Boolean? = null
        while (result.next()) {
            resBool = result.getBoolean("tipomateriale")
        }
        result.close()
        preparedStatement.close()
        if(resBool != null){
            return resBool
        }else{
            throw NoSuchElementException("File non esistente")
        }

    }

}
package com.example.comandiSQL

import com.example.data.Annuncio
import com.example.database.Database
import java.sql.*
import java.text.SimpleDateFormat
import java.time.LocalDate

/*
 * Classe per effettuare operazioni sulla tabella Annunci del dbms
 */
class ComandiAnnuncio(dbms: Database){
    private var database: Database = dbms

    // Metodo per l'inserimento di un nuovo annuncio
    fun insertAdv(annuncio: Annuncio){
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO Annuncio VALUES (?,?,?,?,?,?,?)")
                prepared?.apply {
                    setString(1, annuncio.id)
                    setString(2, annuncio.titolo)
                    setDate(3, java.sql.Date(SimpleDateFormat("yyyy-MM-dd").parse(annuncio.data).time)) //"2024-05-13"
                    setString(4, annuncio.idProprietario)
                    setBoolean(5, annuncio.tipoMateriale)
                    setInt(6, annuncio.areaAnnuncio)
                    setBoolean(7, annuncio.preferito)

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

    // Metodo che restituisce una lista con tutti gli annunci tranne quelli che ha pubblicato l'utente con lo username in input
    fun getListaAnnunci(username: String): ArrayList<Annuncio> {
        try {
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
                    result.getBoolean("tipoMateriale"),
                    result.getString("idProprietarioPersona"),
                    result.getInt("areaAnnuncio"),
                    result.getBoolean("preferito")
                    ))
            }
            result.close()
            preparedStatement.close()
            return listaA
        } catch (e: SQLException) {
            throw e
        }
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    // Metodo che restiruisce tutti gli annunci dell'utente in input, con l'attributo preferito = true
    fun getAnnunciPreferiti(username: String): ArrayList<Annuncio> {
        try {
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
                    result.getBoolean("tipoMateriale"),
                    result.getString("idProprietarioPersona"),
                    result.getInt("areaAnnuncio"),
                    result.getBoolean("preferito")
                ))
            }
            result.close()
            preparedStatement.close()
            return listaA
        } catch (e: SQLException) {
            throw e
        }
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    // Metodo che aggiorna l'attributo preferito all'annuncio con l'id preso in input, con il valore preso in input
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
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setBoolean(1, preferito)
            preparedStatement.setString(2, idAnnuncio)
            preparedStatement.executeUpdate()
            connection.commit()
        } catch (e: SQLException) {
            connection?.rollback()
            throw e
        } finally {
            preparedStatement?.close()
            connection?.autoCommit = true
        }
    }

    // Metodo che elimina dal db l'annuncio con l'id corrispondente
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

    // Metodo che restituisce tutti gli annunci pubblicati dall'utente con lo username in input
    fun getUsernameAnnunci(username: String): ArrayList<Annuncio> {
        try {
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
                    result.getBoolean("tipoMateriale"),
                    result.getString("idProprietarioPersona"),
                    result.getInt("areaAnnuncio"),
                    result.getBoolean("preferito")
                ))
            }
            result.close()
            preparedStatement.close()
            return listaA
        } catch (e: SQLException) {
            throw e
        }
        //se non trova nulla, manda una lista vuota -> non c'è bisogno di un check if(listaA.isNotEmpty)
        //perchè in questo caso viene gestito già dal client
    }

    // Metodo che restituisce true se l'annuncio in input ha un materiale di tipo fisico
    fun isFisico(idA: String): Boolean {
        try {
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
        } catch (e: SQLException) {
            throw e
        }

    }

}
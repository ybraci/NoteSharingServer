package com.example.comandiSQL

import com.example.data.Annuncio
import com.example.database.Database
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat

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

    fun getListaAnnunci(): ArrayList<Annuncio> {
        val query = ("SELECT * "
                + "FROM Annuncio ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
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
    }

    // sets the attribute preferito as true
    fun updatePreferito(idAnnuncio: String, preferito: Boolean) {
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val query = ("UPDATE annuncio SET preferito = ? WHERE id = ? ;")
                val prepared: PreparedStatement? = prepareStatement(query)
                prepared?.apply {
                    setBoolean(1, preferito)
                    setString(2, idAnnuncio)
                    executeUpdate()
                }
                close() // Close the PreparedStatement
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
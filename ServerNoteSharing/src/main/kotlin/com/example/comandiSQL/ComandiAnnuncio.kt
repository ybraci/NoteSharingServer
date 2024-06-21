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

     /*
    //Versione chatgpt
    fun getAnnunciPreferiti(username: String): ArrayList<Annuncio> {
        val listaA: ArrayList<Annuncio> = ArrayList()
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var result: ResultSet? = null

        try {
            connection = database.getConnection()
            if (connection == null || connection.isClosed) {
                throw SQLException("Failed to obtain a valid connection.")
            }

            val query = ("SELECT * FROM Annuncio WHERE idProprietarioPersona = ? AND preferito = true ;")
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, username)

            result = preparedStatement.executeQuery()

            while (result.next()) {
                listaA.add(Annuncio(
                    result.getString("id"),
                    result.getString("titolo"),
                    result.getDate("data").toString(),
                    result.getString("descrizioneAnnuncio"),
                    result.getBoolean("tipoMateriale"),
                    result.getString("idProprietarioPersona"),
                    result.getInt("areaAnnuncio"),
                    result.getBoolean("preferito")
                ))
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            try {
                result?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            try {
                preparedStatement?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            try {
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return listaA
    }

      */



    /*
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
     */

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
            e.printStackTrace()
            throw e
        } finally {
            preparedStatement?.close()
            connection?.autoCommit = true
            connection?.close()
        }
    }




    fun eliminaAnnuncio(idA: String) {
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val query = ("DELETE FROM annuncio WHERE id = ? ;")
                //in cascade viene eliminato anche il corrispondente materiale
                val prepared: PreparedStatement? = prepareStatement(query)
                prepared?.apply {
                    setString(1, idA)
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
    }

}
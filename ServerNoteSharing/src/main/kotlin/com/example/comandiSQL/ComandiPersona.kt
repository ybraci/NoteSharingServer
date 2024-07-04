package com.example.comandiSQL

import com.example.data.CambioPasswordRequest
import com.example.data.Persona
import com.example.database.Database
import io.ktor.server.plugins.*
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*


/*
 * Classe per effettuare operazioni sulla tabella Persona del dbms
 */
class ComandiPersona(dbms: Database) {
    private var database: Database = dbms
    //Metodo per salvare i dati di un nuovo utente
    fun signUp(persona:Persona) {
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO Persona VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")
                prepared?.apply {
                    setString(1, persona.username)
                    setString(2, persona.email)
                    setString(3, persona.password)
                    setString(4, persona.cf)
                    setString(5, persona.nome)
                    setString(6, persona.cognome)
                    setString(7, persona.provincia)
                    setString(8, persona.comune)
                    setString(9, persona.via)
                    setInt(10, persona.nrCivico)
                    setInt(11, persona.cap)
                    setDate(12, parseDate(persona.dataN))

                    executeUpdate()
                    close() // Chiudo PreparedStatement
                }
                commit() // Commit la transaction
            }
        } catch (e: SQLException) {
            // Rollback in caso di eccezioni
            database.getConnection()?.rollback()
            throw e
        } finally {
            // auto-commit a true
            database.getConnection()?.autoCommit = true
        }
    }
    // Metodo per la conversione della stringa in date. Perchè gli oggetti date non erano serializzabili
    private fun parseDate(date: String): Date {
        val formats = arrayOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy/MM/dd")
        for (f in formats){
            try {
                return Date(SimpleDateFormat(f).parse(date).time)
            }catch (e: Exception) {
                continue
            }
        }
        throw IllegalArgumentException("Invalid date format: $date")
    }
    /*
     * Metodo che effettua un aggiornamento della password nella tabella Persona per un utente
     */
    fun cambioP(cambioPRequest: CambioPasswordRequest) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        try {
            connection = database.getConnection()
            if (connection == null || connection.isClosed) {
                throw SQLException("Failed to obtain a connection.")
            }
            connection.autoCommit = false
            val query = "UPDATE Persona SET password=? WHERE username=? AND password=? ; "
            preparedStatement = connection.prepareStatement(query)
            println("Executing query: $query with newPassword=${cambioPRequest.newPassword} and oldPassword=${cambioPRequest.oldPassword} and username=${cambioPRequest.username}")
            preparedStatement.setString(1, cambioPRequest.newPassword)
            preparedStatement.setString(2, cambioPRequest.username)
            preparedStatement.setString(3, cambioPRequest.oldPassword)
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

    // Medodo che controlla le credenziali con quelle nel dbms. True se coincidono
    fun loginUser(username: String, password: String): Boolean {
        try {
            if(username.isBlank() || password.isBlank()){
                return false
            }
            val query = "SELECT username, password FROM Persona WHERE username = ? AND password = ?"
            val preparedStatement = database.getConnection()!!.prepareStatement(query)

            preparedStatement?.apply {
                setString(1, username)
                setString(2, password)
            }
            val result = preparedStatement.executeQuery()
            var usernameResult = ""
            var passwordResult = ""
            while(result.next()) {
                usernameResult = result.getString("username")
                passwordResult = result.getString("password")
            }
            result?.close()
            preparedStatement?.close()
            if(username==usernameResult && password==passwordResult){
                return true
            } else {
                return false
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    // Metodo che verifica se lo username esiste già nel dbms
    fun isUsernameTaken(username: String?): Boolean {
        try {
            val query = "SELECT username FROM Persona WHERE username = ?"
            val preparedStatement = database.getConnection()!!.prepareStatement(query)
            preparedStatement.apply {
                setString(1, username)
            }
            val result = preparedStatement.executeQuery()
            var usernameResult = ""
            while(result.next()) {
                usernameResult = result.getString("username")
            }
            result?.close()
            preparedStatement?.close()
            if(username == usernameResult){
                return true
            } else {
                return false
            }
        } catch (e: SQLException) {
            throw e
        }
    }

    // Metodo che restituisce la mail della persona con lo username di input
    fun getMail(username: String): String {
        try {
            val query = "SELECT email FROM Persona WHERE username = ?"
            val preparedStatement = database.getConnection()!!.prepareStatement(query)

            preparedStatement?.apply {
                setString(1, username)
            }
            val result = preparedStatement.executeQuery()
            var emailResult = ""
            while(result.next()) {
                emailResult = result.getString("email")
            }
            result?.close()
            preparedStatement?.close()
            if(emailResult.isNotBlank()){
                return emailResult
            } else {
                throw NotFoundException()
            }
        } catch (e: SQLException) {
            throw e
        }
    }


}
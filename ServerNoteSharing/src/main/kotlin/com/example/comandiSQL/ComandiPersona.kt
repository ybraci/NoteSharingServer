package com.example.comandiSQL

import com.example.data.Persona
import com.example.database.Database
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*


/**
 * Classe che contiene tutti i metodi che comunicano con la tabella *UtentiRegistrati* della base di dati, effettuando inserimenti, aggiornamenti e interrogazioni.
 *
 * @author Davide Sciacca, matricola nr. 749913, sede VARESE
 * @author Ylli Braci, matricola nr. 749714, sede VARESE
 */
class ComandiPersona(dbms: Database) {
    private var database: Database = dbms

    /**
     * Metodo che inserisce un nuovo utente nella tabella *UtentiRegistrati*. ('E una transazione).
     *
     * @param username Username del nuovo utente.
     * @param email E-mail del nuovo utente.
     * @param password Password del nuovo utente.
     * @param cf Codice Fiscale del nuovo utente.
     * @param nome Nome del nuovo utente.
     * @param cognome Cognome del nuovo utente.
     * @param provincia Provincia di residenza del nuovo utente.
     * @param comune Comune di residenza del nuovo utente.
     * @param via Via di residenza del nuovo utente.
     * @param nrCivico Numero civico di residenza del nuovo utente.
     * @param cap CAP di residenza del nuovo utente.
     * @param dataN Data di nascita del nuovo utente.
     * @throws SQLException Se si verificano errori durante l'interazione con il database.
     */
    @Throws(SQLException::class)
    fun signUp(
        persona:Persona
    ) {
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
    private fun parseDate(date: String): Date {
        val formats = arrayOf("dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", "yyyy/MM/dd")
        for (f in formats){
            try {
                return Date(SimpleDateFormat(f).parse(date).time)

            }catch (e: Exception) {
                // Continue to next format if parsing fails
                continue
            }
        }
        // If none of the formats matched, throw an IllegalArgumentException
        throw IllegalArgumentException("Invalid date format: $date")
    }


    /**
     * Metodo che effettua un aggiornamento della password nella tabella *Persona* per un utente. ('E una transazione).
     *
     * @param newPassword Nuova password.
     * @param email Username dell'utente che vuole aggiornare la password.
     * @throws SQLException Se si verificano errori durante l'interazione con il database.
     */
    @Throws(SQLException::class)
    fun cambioP(newPassword: String?, email: String?) {
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("UPDATE Persona "
                        + "SET password=? "
                        + "WHERE email=?; ")
                prepared?.apply {
                    setString(1, email)
                    setString(2, newPassword)

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

    /**
     * Metodo che effettua una interrogazione nella tabella *UtentiRegistrati* per vedere se lo username dell'utente
     * preso in input è già presente.
     *
     * @param username Username da cercare.
     * @return Restituisce un boolean, che se e true significa che lo username e libero e se restituisce false significa che e gia presente.
     * @throws SQLException Se si verificano errori durante l'interazione con il database.

    @Throws(SQLException::class)
    fun searchUsername(username: String?): Boolean {
    val query = ("SELECT username "
    + "FROM UtentiRegistrati "
    + "WHERE username= ?;")
    val preparedStatement: PreparedStatement = database.getConnection().prepareStatement(query)
    preparedStatement.setString(1, username)

    val result = preparedStatement.executeQuery()

    var usernameTrovati = ""
    while (result.next()) {
    usernameTrovati = usernameTrovati + result.getString("username")
    }
    return usernameTrovati === ""
    //alla fine se esiste sara unico perchè è Primary Key
    }
     */




    /**
     * Metodo che effettua una interrogazione nella tabella *UtentiRegistrati* per trovare l'utente con lo
     * username e la password in input.
     *
     * @param username Username da cercare.
     * @param password Password da cercare.
     * @return Restituisce true se esiste nella tabella un utente con queste credenziali.
     * @throws SQLException Se si verificano errori durante l'interazione con il database.
    @Throws(SQLException::class)
    fun searchUsernamePassword(username: String, password: String): Boolean {
    val query = ("SELECT username, password "
    + "FROM UtentiRegistrati "
    + "WHERE username= ? AND password= ?;")
    val preparedStatement: PreparedStatement = database.getConnection().prepareStatement(query)
    preparedStatement.setString(1, username)
    preparedStatement.setString(2, password)

    val result = preparedStatement.executeQuery()

    //String trovati = "";
    if (result.next()) { //in realtà è solo una riga, visto che i username sono PK
    val usernameTrovato = result.getString("username")
    val passwordTrovato = result.getString("password")
    if (usernameTrovato == username && passwordTrovato == password) {
    return true //username + password corretti
    }
    }
    return false
    }
     */

    fun loginUser(username: String, password: String): Boolean {
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
    }
    //In sostanza, questa funzione verifica se esiste un utente con l’username o l’email e la password forniti.
    // Se esiste, l’utente viene autenticato con successo e la funzione restituisce true.
    // Se non esiste, l’autenticazione fallisce e la funzione restituisce false.

    @Throws(SQLException::class)
    fun isUsernameTaken(username: String?): Boolean {
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
    }


}
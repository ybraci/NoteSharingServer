package com.example.comandiSQL

import com.example.data.Persona
import com.example.database.Database
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.SQLException
import java.text.SimpleDateFormat

/**
 * Classe che contiene tutti i metodi che comunicano con la tabella *UtentiRegistrati* della base di dati, effettuando inserimenti, aggiornamenti e interrogazioni.
 *
 * @author Davide Sciacca, matricola nr. 749913, sede VARESE
 * @author Ylli Braci, matricola nr. 749714, sede VARESE
 */
class ComandiPersona(dbms: Database) {
    private var database: Database = dbms


    @Throws(SQLException::class)
    fun InsertUser(persona: Persona) {

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
                    setDate(12, Date(SimpleDateFormat("yyyy-MM-dd").parse(persona.dataN).time))

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
     * preso in input e gia presente.
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
}
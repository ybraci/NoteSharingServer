package com.example.comandiSQL

import com.example.database.Database
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

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
     * @param email E-mail del nuovo utente.
     * @throws SQLException Se si verificano errori durante l'interazione con il database.
     */
    @Throws(SQLException::class)
    fun signUp(
        email: String?,
        password: String?,
        cf: String?,
        nome: String?,
        cognome: String?,
        provincia: String?,
        comune: String?,
        via: String?,
        nrCivico: Int,
        cap: Int,
        dataN: Date?
    ) {
        try {
            database.getConnection()?.apply {
                autoCommit = false
                val prepared: PreparedStatement? = prepareStatement("INSERT INTO Persona VALUES (?,?,?,?,?,?,?,?,?,?,?)")
                prepared?.apply {
                    setString(1, email)
                    setString(2, password)
                    setString(3, cf)
                    setString(4, nome)
                    setString(5, cognome)
                    setString(6, provincia)
                    setString(7, comune)
                    setString(8, via)
                    setInt(9, nrCivico)
                    setInt(10, cap)
                    setDate(11, dataN)

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

    fun loginUser(usernameOrEmail: String, password: String): Boolean {
        val query = "SELECT * FROM Persona WHERE (username = ? OR email = ?) AND password = ?"
        val connection = database.getConnection()
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        return try {
            preparedStatement = connection?.prepareStatement(query)
            preparedStatement?.apply {
                setString(1, usernameOrEmail) //confrontiamo sia il campo email che il campo username
                setString(2, usernameOrEmail) //confrontiamo sia il campo email che il campo username
                setString(3, password)

                resultSet = executeQuery()
            }
            resultSet?.next() == true // per verificare se esiste almeno una riga nel risultato della query.
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            try {
                resultSet?.close()
                preparedStatement?.close()
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
    //In sostanza, questa funzione verifica se esiste un utente con l’username o l’email e la password forniti.
    // Se esiste, l’utente viene autenticato con successo e la funzione restituisce true.
    // Se non esiste, l’autenticazione fallisce e la funzione restituisce false.

    @Throws(SQLException::class)
    fun isEmailTaken(email: String?): Boolean {
        val query = "SELECT email FROM Persona WHERE email = ?"
        val connection = database.getConnection()
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        return try {
            preparedStatement = connection?.prepareStatement(query)
            preparedStatement?.setString(1, email)
            resultSet = preparedStatement?.executeQuery()
            resultSet?.next() == true
        } catch (e: SQLException) {
            e.printStackTrace()
            throw e
        } finally {
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        }
    }


}
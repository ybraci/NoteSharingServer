package com.example.comandiSQL

import com.example.database.Database

class ComandiAnnunciSalvati(dbms: Database) {
    private var database: Database = dbms

    fun getIdAnnunciSalvati(): ArrayList<String> {
        val query = ("SELECT * "
                + "FROM AnnunciSalvati ;")
        val preparedStatement = database.getConnection()!!.prepareStatement(query)
        val result = preparedStatement.executeQuery()

        val listaA: ArrayList<String> = ArrayList()

        while (result.next()) {
            listaA.add(result.getString("id")
            )
        }
        result.close()
        preparedStatement.close()
        return listaA
    }
}
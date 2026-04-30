package com.example.cardgame.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URI

suspend fun fetchRequest(urlString: String): String{
    return withContext(Dispatchers.IO){
        val url = URI(urlString).toURL()
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000

        connection.getInputStream().bufferedReader().use {reader ->
            reader.readText()
        }
    }
}
package org.aquamarine5.brainspark

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.application.ApplicationStopping
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )
    }
    val database = DatabaseManager(log)

    launch {
        database.startAutoSave()
    }

    monitor.subscribe(ApplicationStopping) {
        runBlocking {
            database.saveToFile()
        }
    }
    configureRouting(database)
}
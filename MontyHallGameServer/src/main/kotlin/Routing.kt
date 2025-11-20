package org.aquamarine5.brainspark

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(databaseManager: DatabaseManager) {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/records"){
            val records = databaseManager.getRecords()
            call.respond(records)
        }

        get("/result") {
            val first = call.request.queryParameters["first"] == "1"
            val second = call.request.queryParameters["second"] == "1"
            val third = call.request.queryParameters["third"] == "1"
            databaseManager.gameFinish(first, second, third)
            call.respondText("Result recorded")
        }
    }
}
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
            val first = call.request.queryParameters["first"]?.toBooleanStrictOrNull()
            val second = call.request.queryParameters["second"]?.toBooleanStrictOrNull()
            val third = call.request.queryParameters["third"]?.toBooleanStrictOrNull()
            databaseManager.gameFinish(first, second, third)
            call.respondText("Result recorded")
        }
    }
}
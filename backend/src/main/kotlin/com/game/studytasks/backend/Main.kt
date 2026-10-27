package com.game.studytasks.backend

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import io.ktor.server.routing.get
import io.ktor.server.response.respondText


fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/ping") {
                call.respondText("pong")
            }
        }
    }.start(wait = true)
}
package com.game.studytasks.backend

import com.game.studytasks.shared.Task
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.toList


fun main() {
    val connectionString = System.getenv("MONGODB_URI") ?: error("Defina a variável MONGODB_URI")
    val client = MongoClient.create(connectionString)
    val database = client.getDatabase("studytasks")
    val tasksCollection = database.getCollection<Task>("tasks")

    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/ping") {
                call.respondText("pong")
            }
            get("/tasks") {
                val tasks = tasksCollection.find().toList()
                call.respond(tasks)
            }
            post("/tasks") {
                val task = call.receive<Task>()
                tasksCollection.insertOne(task)
                call.respond(HttpStatusCode.Created, task)
            }
            put("/tasks/{id}") {
                val id = call.parameters["id"]!!
                val task = call.receive<Task>()
                val filtro = Filters.eq("id", id)

                tasksCollection.replaceOne(filtro, task)
                call.respond(HttpStatusCode.OK, task)
            }
            delete("/tasks/{id}"){
                val id = call. parameters["id"]!!
                val filtro = Filters.eq("id", id)
                tasksCollection.deleteOne(filtro)
                call.respond(HttpStatusCode.NoContent)

            }
        }
    }.start(wait = true)



}
package com.knucklebones.functions

import com.knucklebones.private_values.getIPPub
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class Client {
    var roomId: String = ""
    var roomList: String = ""
    var isHost: Boolean = false
    var turnDone: Boolean = false
    var enemyTurnDone: Boolean = false
    var enemyDiceValue: Int = 0
    var enemyDiceSlot: String = ""
    var hasEnemy: Boolean = false
    private lateinit var server: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    // <|GR|> - Get Rooms
    // <|NR|> - New Room
    // <|CN|> - Connect to Room
    // <|DC|> - Disconnect
    // <|MV|> - Move
    fun connectToServer() {
        runBlocking(Dispatchers.IO) {
            try {
                server = Socket(getIPPub(), 3602)
                writer = PrintWriter(server.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(server.getInputStream()))
            } catch (_: Exception) {  }
        }
    }

}
package com.example.knucklebones.functions

import android.widget.ImageView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class Client {
    //val client = Socket("79.193.88.242", 80)
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
                server = Socket("91.2.208.133", 3602)
                writer = PrintWriter(server.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(server.getInputStream()))
            } catch (_: Exception) {  }
        }
    }

}
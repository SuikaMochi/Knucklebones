package com.example.knucklebones.functions

import android.widget.ImageView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class OldClient {
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

    // GR - Get Rooms
    // NR - New Room
    // CN - Connect to Room
    // DC - Disconnect
    // MV - Move
    fun connectToServer() {
        runBlocking(Dispatchers.IO) {
            try {
                server = Socket("91.2.208.133", 3602)
                writer = PrintWriter(server.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(server.getInputStream()))
                Thread { listenToServer() }.start()
            } catch (_: Exception) {  }
        }
    }

    fun disconnectFromServer() {
        runBlocking(Dispatchers.IO) {
            server.close()
        }
    }

    fun getRooms() {
        runBlocking(Dispatchers.IO) {
            writer.println("GR")
        }
    }

    fun createRoom() {
        runBlocking(Dispatchers.IO) {
            isHost = true

            writer.println("NR")
        }
    }

    fun closeRoom() {
        runBlocking(Dispatchers.IO) {
            isHost = false

            writer.println("CR$roomId")

            roomId = ""
        }
    }

    fun connectToRoom(toRoom: String) {
        runBlocking(Dispatchers.IO) {
            writer.println("CN$$toRoom$")
        }
    }

    fun sendMove(value: Int, slot: String) {
        runBlocking(Dispatchers.IO) {
            turnDone = true
            writer.println("MV$$roomId$$slot$$value")
            enemyTurnDone = false
        }
    }

    fun waitForTurn() {
        runBlocking(Dispatchers.IO) {
            if(!enemyTurnDone) {
                val line = reader.readLine()
                if (line.startsWith("MV")) {
                    enemyTurnDone = true
                    enemyDiceSlot = line.split('$')[1]
                    enemyDiceValue = line.split('$')[2].toInt()
                    turnDone = false
                }
            }
        }
    }

    // GR - Get Rooms
    // NR - New Room
    // CN - Connect to Room
    // DC - Disconnect
    // MV - Move
    private fun listenToServer()
    {
        runBlocking(Dispatchers.IO) {
            while (server.isConnected)
            {
                try {
                    val line = reader.readLine()
                    when (line.take(2)) {
                        "GR" -> roomList = try {
                            line.removePrefix("GR")
                        } catch (_: Exception) {
                            "UTR"
                        }
                        "NR" -> roomId = try {
                            line.removePrefix("NR")
                        } catch (_: Exception) {
                            ""
                        }
                        "CN" -> connectToRoom(roomId)
                        "DC" -> server.close()
                        "MV" -> {
                            enemyTurnDone = true
                            enemyDiceSlot = line.removePrefix("MV").split('$')[1]
                            enemyDiceValue = line.removePrefix("MV").split('$')[2].toInt()
                            turnDone = false
                        }
                    }
                    Thread.sleep(500)
                } catch (_: Exception) {}
            }
        }
    }

    fun waitForEnemy() {
        runBlocking(Dispatchers.IO) {
            while (!hasEnemy)
            {
                Thread.sleep(1000)
            }

            roomId = ""
        }
    }
}
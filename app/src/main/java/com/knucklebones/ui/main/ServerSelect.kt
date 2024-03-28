package com.knucklebones.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.knucklebones.R
import com.knucklebones.functions.Client
import com.knucklebones.functions.Room
import kotlinx.coroutines.*

class ServerSelect : Fragment() {

    companion object {
        fun newInstance(): ServerSelect {
            return ServerSelect()
        }
    }

    private lateinit var client: Client
    private lateinit var viewModel: MainViewModel
    private lateinit var gameSelect: View
    private lateinit var backButton: Button
    private lateinit var createGameButton: Button
    private lateinit var serverList: LinearLayout
    private lateinit var serverView: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        client = Client()
        gameSelect = inflater.inflate(R.layout.serverselect, container, false)

        serverView = gameSelect.findViewById(R.id.main)

        backButton = gameSelect.findViewById(R.id.back)
        createGameButton = gameSelect.findViewById(R.id.create_game)
        serverList = gameSelect.findViewById(R.id.server_list)

        createGameButton.setOnClickListener {  }
        backButton.setOnClickListener { openMainMenu() }

        return gameSelect
    }

    private fun openGameView() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, GameView.newInstance())
            .commitNow()
    }

    private fun openMainMenu() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainMenu.newInstance())
            .commitNow()
    }

}
package com.example.knucklebones.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.knucklebones.R
import com.example.knucklebones.functions.Client
import kotlinx.coroutines.runBlocking

class MainMenu : Fragment() {

    companion object {
        fun newInstance(): MainMenu {
            return MainMenu()
        }
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var mainMenu: View
    private lateinit var gameButton: Button
    private lateinit var serverSearchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainMenu = layoutInflater.inflate(R.layout.mainmenu, container, false)

        gameButton = mainMenu.findViewById(R.id.game)
        serverSearchButton = mainMenu.findViewById(R.id.server_search)

        gameButton.setOnClickListener{ openGameView() }
        serverSearchButton.setOnClickListener{ openServerSearch() }

        return mainMenu
    }

    private fun openGameView() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, GameView.newInstance())
            .commitNow()
    }

    private fun openServerSearch() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ServerSelect.newInstance())
            .commitNow()
    }


}
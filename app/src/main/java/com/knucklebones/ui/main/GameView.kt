package com.knucklebones.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.knucklebones.R
import com.knucklebones.functions.Dice
import com.knucklebones.functions.DiceMat
import com.knucklebones.functions.DiceMatColumn
import com.knucklebones.functions.NPC
import com.knucklebones.functions.calcBlockScore
import com.knucklebones.functions.calcFullScore
import com.knucklebones.functions.isOverlapping
import kotlin.random.Random

@SuppressLint("ClickableViewAccessibility", "SetTextI18n")
class GameView() : Fragment() {
	private lateinit var pDiceLayout: ConstraintLayout
	private lateinit var viewModel: MainViewModel
	private lateinit var gameView: View
	private lateinit var diceTray: ImageView
	private lateinit var newDice: ImageView

	private lateinit var endBannerLayout: ConstraintLayout
	private lateinit var endTextView: TextView
	private lateinit var playAgainButton: Button
	private lateinit var mainMenuButton: Button

	private lateinit var pDiceMat: DiceMat
	private lateinit var pScoreText: TextView

	private lateinit var npc: NPC
	private lateinit var eDiceMat: DiceMat
	private lateinit var eScoreText: TextView

	private lateinit var backButton: Button

	private lateinit var a0: ImageView
	private var currentDice: Int = 0
	private var location = IntArray(2)

	companion object {
		fun newInstance(): GameView {
			return GameView()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = ViewModelProvider(this)[MainViewModel::class.java]
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		gameView = layoutInflater.inflate(R.layout.gameview, container, false)

		endBannerLayout = gameView.findViewById(R.id.end_banner)
		endTextView = gameView.findViewById(R.id.end_text)
		playAgainButton = gameView.findViewById(R.id.play_again_btn)
		mainMenuButton = gameView.findViewById(R.id.main_menu_btn)

		pDiceLayout = gameView.findViewById(R.id.CL_dice)
		a0 = gameView.findViewById(R.id.p_a0)
		backButton = gameView.findViewById(R.id.back_button)

		pScoreText = gameView.findViewById(R.id.p_score_text)
		pDiceMat = DiceMat(
			mutableListOf(
				DiceMatColumn(mutableListOf(
					Dice(a0, 0, 0, 0),
					Dice(gameView.findViewById(R.id.p_a1), 0, 0, 1),
					Dice(gameView.findViewById(R.id.p_a2), 0, 0, 2))
					,0, gameView.findViewById(R.id.p_score_a), 0),
				DiceMatColumn(mutableListOf(
					Dice(gameView.findViewById(R.id.p_b0), 0, 0, 0),
					Dice(gameView.findViewById(R.id.p_b1), 0, 0, 1),
					Dice(gameView.findViewById(R.id.p_b2), 0, 0, 2))
					,0, gameView.findViewById(R.id.p_score_b), 1),
				DiceMatColumn(mutableListOf(
					Dice(gameView.findViewById(R.id.p_c0), 0, 0, 0),
					Dice(gameView.findViewById(R.id.p_c1), 0, 0, 1),
					Dice(gameView.findViewById(R.id.p_c2), 0, 0, 2))
					, 0, gameView.findViewById(R.id.p_score_c), 2)
			)
			, 0)

		npc = NPC.newInstance()
		eScoreText = gameView.findViewById(R.id.e_score_text)
		eDiceMat = DiceMat(
			mutableListOf(
				DiceMatColumn(mutableListOf(
					Dice(gameView.findViewById(R.id.e_a0), 0, 0, 0),
					Dice(gameView.findViewById(R.id.e_a1), 0, 0, 1),
					Dice(gameView.findViewById(R.id.e_a2), 0, 0, 2))
					,0, gameView.findViewById(R.id.e_score_a), 0),
				DiceMatColumn(mutableListOf(
					Dice(gameView.findViewById(R.id.e_b0), 0, 0, 0),
					Dice(gameView.findViewById(R.id.e_b1), 0, 0, 1),
					Dice(gameView.findViewById(R.id.e_b2), 0, 0, 2))
					,0, gameView.findViewById(R.id.e_score_b), 1),
				DiceMatColumn(mutableListOf(
					Dice(gameView.findViewById(R.id.e_c0), 0, 0, 0),
					Dice(gameView.findViewById(R.id.e_c1), 0, 0, 1),
					Dice(gameView.findViewById(R.id.e_c2), 0, 0, 2))
					, 0, gameView.findViewById(R.id.e_score_c), 2)
			)
			, 0)

		diceTray = gameView.findViewById(R.id.dice_tray)
		diceTray.setOnClickListener { rollDice() }
		backButton.setOnClickListener { openMainMenu() }
		gameView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
			pScoreText.text = "You: ${calcFullScore(pDiceMat)}"
			pDiceMat.column[0].rowScoreText.text = "${calcBlockScore(pDiceMat.column[0])}"
			pDiceMat.column[1].rowScoreText.text = "${calcBlockScore(pDiceMat.column[1])}"
			pDiceMat.column[2].rowScoreText.text = "${calcBlockScore(pDiceMat.column[2])}"

			eScoreText.text = "${npc.getName()}: ${calcFullScore(eDiceMat)}"
			eDiceMat.column[0].rowScoreText.text = "${calcBlockScore(eDiceMat.column[0])}"
			eDiceMat.column[1].rowScoreText.text = "${calcBlockScore(eDiceMat.column[1])}"
			eDiceMat.column[2].rowScoreText.text = "${calcBlockScore(eDiceMat.column[2])}"
		}

		endBannerLayout.visibility = View.INVISIBLE
		mainMenuButton.setOnClickListener { openMainMenu() }
		playAgainButton.setOnClickListener { reopenGameView() }

		return gameView
	}

	//Rolls the dice, new random value and retrieve image
	private fun rollDice() {
		diceTray.getLocationOnScreen(location)
		diceTray.isClickable = false
		val num = (1..6).random(Random(System.currentTimeMillis()))
		newDiceImg(num)
		currentDice = num
	}

	//Initiated a new Dice Image for the rolled dice
	private fun newDiceImg(i: Int) {
		diceTray.getLocationOnScreen(location)
		newDice = ImageView(context)
		gameView.findViewById<ConstraintLayout>(R.id.foreGroundLayer).addView(newDice)

		newDice.setImageResource(getDiceImg(i))
		newDice.layoutParams.height = a0.height
		newDice.layoutParams.width = a0.width
		newDice.x = location[0].toFloat() + (diceTray.width/2 - a0.width/2).toFloat()
		newDice.y = location[1].toFloat() + (diceTray.height/3 - a0.height/2).toFloat()

		newDice.translationZ = 5f
		newDice.isClickable = true
		newDice.setOnTouchListener(dragAndDrop)
	}

	//Switch-Case for the rolled dice value getting the correct image
	private fun getDiceImg(i: Int): Int {
		return when (i) {
			6 -> R.drawable.dice_six
			5 -> R.drawable.dice_five
			4 -> R.drawable.dice_four
			3 -> R.drawable.dice_three
			2 -> R.drawable.dice_two
			else -> R.drawable.dice_one
		}
	}

	//Player makes their move, literally, controls rolled dice movement and placement
	private var dragAndDrop = View.OnTouchListener(function = { view, motionEvent ->
		kotlin.run breaking@ {
			if (motionEvent.action == MotionEvent.ACTION_MOVE) {
				view.x = motionEvent.rawX - view.width/2
				view.y = motionEvent.rawY - view.height
			}
			if(motionEvent.action == MotionEvent.ACTION_UP) {
				pDiceMat.column.forEach {
					it.row.forEach { itt ->
						val curr = IntArray(2)
						val slot = IntArray(2)
						view.getLocationOnScreen(curr)
						itt.slot.getLocationOnScreen(slot)

						if (isOverlapping(curr, slot, itt, view) && it.full == 0) {
							var tmp = 0
							it.row.forEach {sl ->
								if (sl.isSet == 0) {
									diceTray.isClickable = false

									sl.slot.setImageResource(getDiceImg(currentDice))
									sl.isSet = 1
									sl.value = currentDice
									newDice.visibility = View.INVISIBLE
									diceAttack(sl, eDiceMat, it.columnNr)

									theTracker(eDiceMat)
									if (!isGameEnd(pDiceMat))
									{
										npcTurn()
									}

									diceTray.isClickable = true

									return@breaking
								}
								else {
									tmp+=1
								}
							}
							if (tmp == 3) {
								it.full = 1
							}
						}
					}
				}
			}
		}
		true
	})

	//Make this shit scalable and less fucked so it works better with NPC
	//NPC makes their turn based on their play type
	private fun npcTurn() {
		kotlin.run breaking@ {
			val nDice = (1..6).random(Random(System.currentTimeMillis()))
			//NPC decision not yet working correctly?
			//Something is not working somewhere?
			//Why is it deleting everything when row 3 is chosen?
			//What the fuck please help?
			val nColumn = npc.makeDecision(eDiceMat, pDiceMat, nDice)

			eDiceMat.column[nColumn].row.forEach { sl ->
				if (sl.isSet == 0) {
					sl.slot.setImageResource(getDiceImg(nDice))
					sl.isSet = 1
					sl.value = nDice
					diceAttack(sl, pDiceMat, nColumn)

					println("nColumn: $nColumn")
					println("nDice: $nDice")

					theTracker(pDiceMat)
					isGameEnd(eDiceMat)

					Thread.sleep(1000)
					return@breaking
				}
			}
		}
	}

	//New dice "attacking" the row of the other side
	private fun diceAttack(attackerDice: Dice, defenderMat: DiceMat, columnNr: Int) {
		println("____________\nDice Attack\n____________\n")
		println("attackerDice: ${attackerDice.value}")

		defenderMat.column[columnNr].row.forEach {
			if (it.value == attackerDice.value)
			{
				println("defenderRow: ${it.value}")
				println("defenderRow: ${it.slotNr}")
				it.isSet = 0
				it.value = 0
				it.slot.setImageResource(0)

			}
		}
	}

	//Tracks the columns and sets full state accordingly
	private fun theTracker(toTrack: DiceMat) {
		var columnCounter = 0

		toTrack.column.forEach {
			var rowCounter = 0
			it.row.forEach { itt ->
				rowCounter += itt.isSet
				if (itt.isSet == 0)
				{
					itt.value = 0
					itt.slot.setImageResource(0)
				}
			}
			if (rowCounter == 3)
			{
				it.full = 1
				columnCounter++
			}
			else { it.full = 0 }
		}
		if (columnCounter == 3)
		{ toTrack.full = 1 }
		else { toTrack.full = 0 }
	}

	//Check if a mat is full and end the game
	private fun isGameEnd(matToCheck: DiceMat): Boolean {
		var fullBlock = 0
		matToCheck.column.forEach {
			it.row.forEach { itt ->
				fullBlock += itt.isSet
			}
		}
		if (fullBlock == 9)
		{
			diceTray.isClickable = false
			backButton.visibility = View.INVISIBLE

			if (calcFullScore(pDiceMat) > calcFullScore(eDiceMat))
			{
				endTextView.text = "YOU WON!"
			}
			else
			{
				endTextView.text = "${npc.getName()} WON!"
			}
			endBannerLayout.visibility = View.VISIBLE
			return true
		}
		return false
	}

	//Yeah... Opens the Main Menu back up, going back
	private fun openMainMenu() {
		requireActivity()
			.supportFragmentManager
			.beginTransaction()
			.replace(R.id.container, MainMenu.newInstance())
			.commitNow()
	}

	private fun reopenGameView() {
		requireActivity()
			.supportFragmentManager
			.beginTransaction()
			.replace(R.id.container, newInstance())
			.commitNow()
	}
}
package com.example.knucklebones.ui.main

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
import com.example.knucklebones.R
import com.example.knucklebones.functions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

@SuppressLint("ClickableViewAccessibility", "SetTextI18n")
class GameViewOnline(private val client: Client) : Fragment() {

    private lateinit var pDiceLayout: ConstraintLayout
    private lateinit var viewModel: MainViewModel
    private lateinit var gameView: View
    private lateinit var diceTray: ImageView
    private lateinit var newDice: ImageView

    private lateinit var pDiceMat: DiceMat
    private lateinit var pScoreText: TextView

    private lateinit var eDiceMat: DiceMat
    private lateinit var eScoreText: TextView

    private lateinit var backButton: Button
    private var isTurn: Boolean = false

    private lateinit var a0: ImageView
    private var currentDice: Int = 0
    private var location = IntArray(2)

    companion object {
        fun newInstance(client: Client): GameViewOnline {
            return GameViewOnline(client)
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
                    ,0, gameView.findViewById(R.id.p_score_a), 1),
                DiceMatColumn(mutableListOf(
                    Dice(gameView.findViewById(R.id.p_b0), 0, 0, 0),
                    Dice(gameView.findViewById(R.id.p_b1), 0, 0, 1),
                    Dice(gameView.findViewById(R.id.p_b2), 0, 0, 2))
                    ,0, gameView.findViewById(R.id.p_score_b), 2),
                DiceMatColumn(mutableListOf(
                    Dice(gameView.findViewById(R.id.p_c0), 0, 0, 0),
                    Dice(gameView.findViewById(R.id.p_c1), 0, 0, 1),
                    Dice(gameView.findViewById(R.id.p_c2), 0, 0, 2))
                    , 0, gameView.findViewById(R.id.p_score_c), 3)
            )
            , 0)

        eScoreText = gameView.findViewById(R.id.e_score_text)
        eDiceMat = DiceMat(
            mutableListOf(
                DiceMatColumn(mutableListOf(
                    Dice(gameView.findViewById(R.id.e_a0), 0, 0, 0),
                    Dice(gameView.findViewById(R.id.e_a1), 0, 0, 1),
                    Dice(gameView.findViewById(R.id.e_a2), 0, 0, 2))
                    ,0, gameView.findViewById(R.id.e_score_a), 1),
                DiceMatColumn(mutableListOf(
                    Dice(gameView.findViewById(R.id.e_b0), 0, 0, 0),
                    Dice(gameView.findViewById(R.id.e_b1), 0, 0, 1),
                    Dice(gameView.findViewById(R.id.e_b2), 0, 0, 2))
                    ,0, gameView.findViewById(R.id.e_score_b), 2),
                DiceMatColumn(mutableListOf(
                    Dice(gameView.findViewById(R.id.e_c0), 0, 0, 0),
                    Dice(gameView.findViewById(R.id.e_c1), 0, 0, 1),
                    Dice(gameView.findViewById(R.id.e_c2), 0, 0, 2))
                    , 0, gameView.findViewById(R.id.e_score_c), 3)
            )
            , 0)

        diceTray = gameView.findViewById(R.id.dice_tray)
        diceTray.setOnClickListener { rollDice() }
        backButton.setOnClickListener { openServerSelect() }
        gameView.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
            pScoreText.text = "Score: ${calcFullScore(pDiceMat)}"
            pDiceMat.column[0].rowScoreText.text = "${calcBlockScore(pDiceMat.column[0])}"
            pDiceMat.column[1].rowScoreText.text = "${calcBlockScore(pDiceMat.column[1])}"
            pDiceMat.column[2].rowScoreText.text = "${calcBlockScore(pDiceMat.column[2])}"


            eScoreText.text = "Score: ${calcFullScore(eDiceMat)}"
            eDiceMat.column[0].rowScoreText.text = "${calcBlockScore(eDiceMat.column[0])}"
            eDiceMat.column[1].rowScoreText.text = "${calcBlockScore(eDiceMat.column[1])}"
            eDiceMat.column[2].rowScoreText.text = "${calcBlockScore(eDiceMat.column[2])}"
        }

        return gameView
    }

    private fun rollDice()
    {
        diceTray.getLocationOnScreen(location)
        diceTray.isClickable = false
        val num = (1..6).random(Random(System.currentTimeMillis()))
        newDiceImg(num)
        currentDice = num
    }

    private fun newDiceImg(i: Int) {
        diceTray.getLocationOnScreen(location)
        newDice = ImageView(context)
        gameView.findViewById<ConstraintLayout>(R.id.foreGroundLayer).addView(newDice)

        newDice.setImageResource(getDiceImg(i))
        newDice.layoutParams.height = a0.height
        newDice.layoutParams.width = a0.width
        newDice.x = location[0].toFloat() + (diceTray.width/2 - a0.width/2).toFloat()
        newDice.y = location[1].toFloat() + (diceTray.height/3 - a0.height/2).toFloat()

        newDice.isClickable = true
        newDice.setOnTouchListener(dragAndDrop)
    }

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
                                    val slotId = resources.getResourceEntryName(sl.slot.id).replace('a', 'e')

                                    runBlocking(Dispatchers.IO) {

                                        //waitForTurn()
                                    }

                                    diceTray.isClickable = false

                                    sl.slot.setImageResource(getDiceImg(currentDice))
                                    sl.isSet = 1
                                    sl.value = currentDice
                                    newDice.visibility = View.INVISIBLE
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

    private fun waitForTurn() {
        runBlocking(Dispatchers.IO) {
            Thread.sleep(500)

            if (client.enemyTurnDone) {
                when (client.enemyDiceSlot) {
                    "e_a0" -> setEnemyDice(0, 0, client.enemyDiceValue)
                    "e_a1" -> setEnemyDice(0, 1, client.enemyDiceValue)
                    "e_a2" -> setEnemyDice(0, 2, client.enemyDiceValue)

                    "e_b0" -> setEnemyDice(1, 0, client.enemyDiceValue)
                    "e_b1" -> setEnemyDice(1, 1, client.enemyDiceValue)
                    "e_b2" -> setEnemyDice(1, 2, client.enemyDiceValue)

                    "e_c0" -> setEnemyDice(2, 0, client.enemyDiceValue)
                    "e_c1" -> setEnemyDice(2, 1, client.enemyDiceValue)
                    "e_c2" -> setEnemyDice(2, 2, client.enemyDiceValue)
                }
                diceTray.isClickable = true

            }
            else {
                waitForTurn()
            }
        }
    }

    private fun setEnemyDice(block: Int, slot: Int, value: Int) {
        runBlocking(Dispatchers.Main) {
            eDiceMat.column[block].row[slot].value = value
            eDiceMat.column[block].row[slot].isSet = 1
            eDiceMat.column[block].row[slot].slot.setImageResource(getDiceImg(value))
        }
    }



    private fun openServerSelect() {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, ServerSelect.newInstance())
            .commitNow()
    }
}
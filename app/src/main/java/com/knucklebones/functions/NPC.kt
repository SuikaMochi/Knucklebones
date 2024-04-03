package com.knucklebones.functions

import kotlin.math.abs
import kotlin.random.Random

class NPC(type: Int) {
	private val names = mutableListOf("Kai", "Bob", "Yok")
	private var npcName = names.random(Random(System.currentTimeMillis()))
	private val npcType = type
	private var decision = 0
	private var threshold = 0

	private lateinit var nFutureMat: DiceMat
	private var nColumnsScores = mutableListOf<Int>(0, 0, 0)
	private var nFullScore = 0
	private var nFreeColumns = mutableListOf<Int>()

	private lateinit var pFutureMat: DiceMat
	private var pColumnsScores = mutableListOf<Int>(0, 0, 0)
	private var pFullScore = 0

	companion object {
		fun newInstance(): NPC {
			return NPC(((1..3).random(Random(System.currentTimeMillis()))))
		}
	}

	fun getName(): String {
		return npcName
	}

	//1 Balanced: 50% attack, 50% points
	//2 Attack:   80% attack, 20% points
	//3 Points:   20% attack, 80% points
	//NPC sends the column after a "calculated" decision, I guess
	//TODO: make npc decide based on percentage
	// How to calculate it?
	// What data do I need?
	// Make scalable for more types
	// LATER! Add type that learns from player (much later)
	// Please end me
	fun makeDecision(npcMat: DiceMat, playerMat: DiceMat, nDice: Int): Int {
		threshold = when (npcType) {
			3 ->	20
			2 ->	80
			else ->	50
		}

		nFutureMat = npcMat
		pFutureMat = playerMat

		decision = ((1..100).random(Random(System.currentTimeMillis())))

		nColumnsScores[0] = calcBlockScore(npcMat.column[0])
		nColumnsScores[1] = calcBlockScore(npcMat.column[1])
		nColumnsScores[2] = calcBlockScore(npcMat.column[2])
		nFullScore = nColumnsScores[0] + nColumnsScores[1] + nColumnsScores[2]

		npcMat.column.forEach {
			if (it.full == 0)
			{
				nFreeColumns.add(it.columnNr)
			}
		}

		pColumnsScores[0] = calcBlockScore(playerMat.column[0])
		pColumnsScores[1] = calcBlockScore(playerMat.column[1])
		pColumnsScores[2] = calcBlockScore(playerMat.column[2])
		pFullScore = pColumnsScores[0] + pColumnsScores[1] + pColumnsScores[2]

		return if (decision < threshold) {
			attackPlayer(npcMat, playerMat, nDice)
		}
		else {
			getPoints(npcMat, nDice)
		}
	}

	private fun attackPlayer(npcMat: DiceMat, playerMat: DiceMat, nDice: Int): Int {
		val ftrScores = mutableListOf(0, 0, 0)
		val lisScores = mutableListOf(mutableListOf(0, 0), mutableListOf(0, 0), mutableListOf(0, 0))

		playerMat.column.forEach {
			it.row.forEach { itt ->
				if (itt.value == nDice) {
					pFutureMat.column[it.columnNr].row[itt.slotNr].value = 0
					pFutureMat.column[it.columnNr].row[itt.slotNr].isSet = 0
				}
			}
		}

		ftrScores[0] = calcBlockScore(pFutureMat.column[0])
		ftrScores[1] = calcBlockScore(pFutureMat.column[1])
		ftrScores[2] = calcBlockScore(pFutureMat.column[2])
/*
		print("____________\nAttack\n____________\n")

		println("ftrScores_0: ${ftrScores[0]}")
		println("ftrScores_1: ${ftrScores[1]}")
		println("ftrScores_2: ${ftrScores[2]}")
*/
		lisScores[0][0] =
			if (pColumnsScores[0] > ftrScores[0]) { abs(pColumnsScores[0] - ftrScores[0]) }
			else { 0 }
		lisScores[0][1] = 0

		lisScores[1][0] =
			if (pColumnsScores[1] > ftrScores[1]) { abs(pColumnsScores[1] - ftrScores[1]) }
			else { 0 }
		lisScores[1][1] = 1

		lisScores[2][0] =
			if (pColumnsScores[2] > ftrScores[2]) { abs(pColumnsScores[2] - ftrScores[2]) }
			else { 0 }
		lisScores[2][1] = 2
/*
		println("lisScores_0: ${lisScores[0]}")

		println("lisScores_1: ${lisScores[1]}")

		println("lisScores_2: ${lisScores[2]}")

		println("lisScores_0 Sorted: ${lisScores.sortedByDescending{ it[0] }[0]}")

		println("lisScores_1 Sorted: ${lisScores.sortedByDescending{ it[0] }[1]}")

		println("lisScores_2 Sorted: ${lisScores.sortedByDescending{ it[0] }[2]}")
*/
		if (lisScores[0][0] == 0 && lisScores[1][0] == 0 && lisScores[2][0] == 0)
		{
			return getPoints(npcMat, nDice)
		}

		lisScores.sortedByDescending { it[0] }.forEach {
			if (npcMat.column[it[1]].full != 1)
			{
				return it[1]
			}
		}
		return getPoints(npcMat, nDice)
	}

	private fun getPoints(npcMat: DiceMat, nDice: Int): Int{
		val ftrScores = mutableListOf(0, 0, 0)
		val lisScores = mutableListOf(mutableListOf(0, 0), mutableListOf(0, 0), mutableListOf(0, 0))

		npcMat.column.forEach {
			if (it.full == 0) {
				it.row.first { itt -> itt.isSet == 0 }.value = nDice
			}
		}

		ftrScores[0] = calcBlockScore(nFutureMat.column[0])
		ftrScores[1] = calcBlockScore(nFutureMat.column[1])
		ftrScores[2] = calcBlockScore(nFutureMat.column[2])
/*
		print("____________\nPoints\n____________\n")

		println("ftrScores_0: ${ftrScores[0]}")
		println("ftrScores_1: ${ftrScores[1]}")
		println("ftrScores_2: ${ftrScores[2]}")
*/
		lisScores[0][0] =
			if (nColumnsScores[0] < ftrScores[0]) { ftrScores[0] }
			else { 0 }
		lisScores[0][1] = 0

		lisScores[1][0] =
			if (nColumnsScores[1] < ftrScores[1]) { ftrScores[1] }
			else { 0 }
		lisScores[1][1] = 1

		lisScores[2][0] =
			if (nColumnsScores[2] < ftrScores[2]) { ftrScores[2] }
			else { 0 }
		lisScores[2][1] = 2
/*
		println("lisScores_0: ${lisScores[0]}")

		println("lisScores_1: ${lisScores[1]}")

		println("lisScores_2: ${lisScores[2]}")

		println("lisScores_0 Sorted: ${lisScores.sortedByDescending{ it[0] }[0]}")

		println("lisScores_1 Sorted: ${lisScores.sortedByDescending{ it[0] }[1]}")

		println("lisScores_2 Sorted: ${lisScores.sortedByDescending{ it[0] }[2]}")
*/
		lisScores.sortedByDescending { it[0] }.forEach {
			if (npcMat.column[it[1]].full != 1)
			{
				return it[1]
			}
		}
		return nFreeColumns.random(Random(System.currentTimeMillis()))
	}
}
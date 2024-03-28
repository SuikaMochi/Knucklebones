package com.example.knucklebones.functions

//2 of a kind - N x 2
//3 of a kind - N x 3
fun calcFullScore(mat: DiceMat): Int
{
	return calcBlockScore(mat.column[0]) + calcBlockScore(mat.column[1]) + calcBlockScore(mat.column[2])
}

fun calcBlockScore(block: DiceMatColumn): Int
{
	val l = mutableListOf(0,0,0,0,0,0,0)
	var score = 0

	block.row.forEach{
		when (it.value)
		{
			1 -> l[1]+=1
			2 -> l[2]+=1
			3 -> l[3]+=1
			4 -> l[4]+=1
			5 -> l[5]+=1
			6 -> l[6]+=1
		}
	}

	block.row.forEach {
		score += (it.value * l[it.value])
	}
	return score
}

fun hasDuplicate(block: DiceMatColumn): Int
{
	val l = mutableListOf(0,0,0,0,0,0,0)
	block.row.forEach{
		when (it.value)
		{
			1 -> l[1]+=1
			2 -> l[2]+=1
			3 -> l[3]+=1
			4 -> l[4]+=1
			5 -> l[5]+=1
			6 -> l[6]+=1
		}
	}

	var temp = 0

	l.forEach {
		if (it > temp)
		{
			temp = it
		}
	}

	return temp
}

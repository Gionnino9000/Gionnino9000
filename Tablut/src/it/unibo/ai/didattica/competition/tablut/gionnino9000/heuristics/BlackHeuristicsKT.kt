package it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics

import it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics.Heuristics
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut
import it.unibo.ai.didattica.competition.tablut.domain.State

class BlackHeuristicsKT(state: State?) : Heuristics(state) {
    private val WHITE_EATEN = 0
    private val BLACK_ALIVE = 1
    private val BLACK_SUR_K = 2
    private val RHOMBUS_POS = 3
    private val BLOCKED_ESC = 3

    // Flag to enable console print
    private val print = false

    // Numb. tiles in rhombus
    private val TILES_IN_RHOMBUS = 8

    // Weights for evaluation in the following order: WhiteEaten, BlackAlive, BlackSurroundingKing, RhombusPos
    private val earlyGameWeights: Array<Double?>
    private val lateGameWeights: Array<Double?>

    // Matrix of favourite black positions in the initial stages to block the escape ways
    private val rhombus = arrayOf(
        intArrayOf(1, 2),
        intArrayOf(1, 6),
        intArrayOf(2, 1),
        intArrayOf(2, 7),
        intArrayOf(6, 1),
        intArrayOf(6, 7),
        intArrayOf(7, 2),
        intArrayOf(7, 6)
    )

    init {
        earlyGameWeights = arrayOfNulls(4)
        earlyGameWeights[WHITE_EATEN] = 50.0
        earlyGameWeights[BLACK_ALIVE] = 35.0
        earlyGameWeights[BLACK_SUR_K] = 10.0
        earlyGameWeights[RHOMBUS_POS] = 5.0
        lateGameWeights = arrayOfNulls(4)
        lateGameWeights[WHITE_EATEN] = 40.0
        lateGameWeights[BLACK_ALIVE] = 30.0
        lateGameWeights[BLACK_SUR_K] = 25.0
        lateGameWeights[BLOCKED_ESC] = 5.0
    }

    /**
     * @return the evaluation of the states using a weighted sum
     */
    override fun evaluateState(): Double {
        var stateValue = 0.0
        var lateGame = false
        val numbOfWhite = state.getNumberOf(State.Pawn.WHITE)
        if (numbOfWhite <= 4) lateGame = true

        // Values for the weighted sum
        val numberOfBlackAlive = state.getNumberOf(State.Pawn.BLACK).toDouble() / GameAshtonTablut.NUM_BLACK
        val numberOfWhiteEaten = (GameAshtonTablut.NUM_WHITE - numbOfWhite).toDouble() / GameAshtonTablut.NUM_WHITE
        val surroundKing =
            checkAdjacentPawns(state, kingPosition(state), State.Turn.BLACK.toString()).toDouble() / getNumbToEatKing(
                state
            )
        if (print) {
            println("Black pawns alive: $numberOfBlackAlive")
            println("Number of pawns near to the king:$surroundKing")
            println("Number of white pawns eaten: $numberOfWhiteEaten")
        }
        if (!lateGame) {
            val pawnsInRhombus = rhombusValue.toDouble() / TILES_IN_RHOMBUS
            stateValue += numberOfWhiteEaten * earlyGameWeights[WHITE_EATEN]!!
            stateValue += numberOfBlackAlive * earlyGameWeights[BLACK_ALIVE]!!
            stateValue += surroundKing * earlyGameWeights[BLACK_SUR_K]!!
            stateValue += pawnsInRhombus * earlyGameWeights[RHOMBUS_POS]!!
            if (print) {
                println("Number on rhombus pos: $pawnsInRhombus")
                println("|EARLY_GAME|: value is $stateValue")
            }
        } else {
            val blockingPawns = blockingPawns().toDouble()
            stateValue += numberOfWhiteEaten * lateGameWeights[WHITE_EATEN]!!
            stateValue += numberOfBlackAlive * lateGameWeights[BLACK_ALIVE]!!
            stateValue += surroundKing * lateGameWeights[BLACK_SUR_K]!!
            stateValue += blockingPawns * lateGameWeights[BLOCKED_ESC]!!
            if (print) {
                println("Blocking pawns: $blockingPawns")
                println("|LATE_GAME|: value is $stateValue")
            }
        }
        return stateValue
    }

    /**
     * @return Number of black pawns on tiles if we have enough pawns
     */
    val rhombusValue: Int
        get() = if (state.getNumberOf(State.Pawn.BLACK) >= 10) {
            pawnsInRhombus()
        } else {
            0
        }

    /**
     * @return Number of black pawns on rhombus configuration
     */
    fun pawnsInRhombus(): Int {
        var count = 0
        for (position in rhombus) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++
            }
        }
        return count
    }

    /**
     * @return Number of pawns blocking king escape
     */
    fun blockingPawns(): Int {
        return 4 - countKingEscapes(state)
    }
}
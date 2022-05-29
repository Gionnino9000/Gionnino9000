package it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Arrays;

/**
 * Heuristics for the evaluation of a black player state.<br/>
 *
 * Description: the attacker (black player) heuristics is
 * based on a weighted sum, with 5 different weights (WHITE_EATEN,
 * BLACK_ALIVE, BLACK_SUR_K, RHOMBUS_POS, BLOCKED_ESC) and
 * bonuses in special cases.<br/>
 * The key feature of the evaluation is based on the
 * adoption of 2 different set of weights depending on
 * the game phase:
 * <ul>
 *     <li><b>early game</b>, we focus on a more defensive
 *     approach, trying to eat as many enemy pawns as
 *     possible, and trying to keep several allied pawns alive
 *     at the same time. In addition to that, we also try
 *     to get into a rhombus formation, to prevent the
 *     king from escaping;</li>
 *     <li><b>late game</b>, we focus on a more aggressive
 *     approach, giving more weight to the king surrounding
 *     and blocking his escapes.</li>
 * </ul>
 * We switch to late game when the white pawn number
 * is below 5.
 *
 * @author Gionnino9000
 */
public class BlackHeuristics extends Heuristics {

    private final int WHITE_EATEN = 0; // White pawns already eaten
    private final int BLACK_ALIVE = 1; // Black pawns still alive
    private final int BLACK_SUR_K = 2; // Black pawns surrounding the king
    private final int RHOMBUS_POS = 3; // Formation to prevent the king from escaping
    private final int BLOCKED_ESC = 3; // Pawns that block king escapes

    private final double PAWNS_AGGRESSION_WEIGHT = 2.0;

    // Flag to enable console print
    private boolean print = false;

    // Number of tiles in rhombus
    private final int TILES_IN_RHOMBUS = 8;

    private final Double[] earlyGameWeights;
    private final Double[] lateGameWeights;

    // Matrix of favourite black positions in the initial stages to block the escape ways
    private final int[][] rhombus = {
                  {1,2},       {1,6},
            {2,1},                   {2,7},

            {6,1},                   {6,7},
                  {7,2},       {7,6}
    };

    public BlackHeuristics(State state) {
        super(state);

        earlyGameWeights = new Double[4];
        earlyGameWeights[WHITE_EATEN] = 45.0;
        earlyGameWeights[BLACK_ALIVE] = 35.0;
        earlyGameWeights[BLACK_SUR_K] = 15.0;
        earlyGameWeights[RHOMBUS_POS] = 5.0;

        lateGameWeights = new Double[4];
        lateGameWeights[WHITE_EATEN] = 40.0;
        lateGameWeights[BLACK_ALIVE] = 30.0;
        lateGameWeights[BLACK_SUR_K] = 25.0;
        lateGameWeights[BLOCKED_ESC] = 5.0;
    }

    /**
     * @return the evaluation of the current state using a weighted sum
     */
    @Override
    public double evaluateState() {
        int[] kingPos = kingPosition(state);

        double stateValue = 0.0;
        boolean lateGame = false;

        int numbOfWhite = state.getNumberOf(State.Pawn.WHITE);
        if (numbOfWhite <= 4)
            lateGame = true;

        // Values for the weighted sum
        double numberOfBlackAlive = (double) state.getNumberOf(State.Pawn.BLACK) / GameAshtonTablut.NUM_BLACK;
        double numberOfWhiteEaten = (double) (GameAshtonTablut.NUM_WHITE - numbOfWhite) / GameAshtonTablut.NUM_WHITE;
        double surroundKing = (double) checkAdjacentPawns(state, kingPos, State.Turn.BLACK.toString()) / getNumbToEatKing(state);

        double whiteInDanger = getPawnsAggression();
        if (whiteInDanger > 0)
            stateValue += (whiteInDanger / numbOfWhite) * PAWNS_AGGRESSION_WEIGHT;

        if (print) {
            System.out.println("Black pawns alive: " + numberOfBlackAlive);
            System.out.println("Number of pawns near to the king:" + surroundKing);
            System.out.println("Number of white pawns eaten: " + numberOfWhiteEaten);
        }

        if (!lateGame) { // Early Game
            double pawnsInRhombus = (double) getRhombusValue() / TILES_IN_RHOMBUS;

            stateValue += numberOfWhiteEaten * earlyGameWeights[WHITE_EATEN];
            stateValue += numberOfBlackAlive * earlyGameWeights[BLACK_ALIVE];
            stateValue += surroundKing * earlyGameWeights[BLACK_SUR_K];
            stateValue += pawnsInRhombus * earlyGameWeights[RHOMBUS_POS];

            if (print) {
                System.out.println("Number on rhombus pos: " + pawnsInRhombus);
                System.out.println("|EARLY_GAME|: value is " + stateValue);
            }
        } else { // Late Game
            double blockingPawns = (double) blockingPawns();

            stateValue += numberOfWhiteEaten * lateGameWeights[WHITE_EATEN];
            stateValue += numberOfBlackAlive * lateGameWeights[BLACK_ALIVE];
            stateValue += surroundKing * lateGameWeights[BLACK_SUR_K];
            stateValue += blockingPawns * lateGameWeights[BLOCKED_ESC];

            stateValue += canCaptureKing();

            if (print) {
                System.out.println("Blocking pawns: " + blockingPawns);
                System.out.println("|LATE_GAME|: value is " + stateValue);
            }
        }

        return stateValue;
    }

    /**
     * @return the number of black pawns on tiles if we have enough pawns
     */
    public int getRhombusValue() {
        if (state.getNumberOf(State.Pawn.BLACK) >= 10) {
            return pawnsInRhombus();
        } else {
            return 0;
        }
    }

    /**
     * @return the number of black pawns on rhombus configuration
     */
    public int pawnsInRhombus() {
        int count = 0;

        for (int[] position : rhombus) {
            if (state.getPawn(position[0], position[1]).equalsPawn(State.Pawn.BLACK.toString())) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the number of pawns blocking king escape
     */
    public int blockingPawns() {
        return 4 - Arrays.stream(getKingEscapes(state, kingPosition(state))).sum();
    }

    /**
     * Used to add value to the position if we create win threats
     *
     * @return 10.0 if the king can be captured, 0.0 otherwise
     */
    private double canCaptureKing() {
        if (canBeCaptured(state, kingPosition(state), State.Pawn.KING))
            return +10.0;

        return 0.0;
    }

    /**
     * @return the number of white pawns that can be captured
     */
    private int getPawnsAggression() {
        int capturable = 0;

        State.Pawn[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equalsPawn(State.Pawn.WHITE.toString())) {
                    capturable += canBeCaptured(state, new int[]{i, j}, State.Pawn.WHITE) ? 1 : 0;
                }
            }
        }

        return capturable;
    }

    // Add pawn per quadrant distribution evaluation ?
}

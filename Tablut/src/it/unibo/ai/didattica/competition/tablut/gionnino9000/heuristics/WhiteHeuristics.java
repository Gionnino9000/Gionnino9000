package it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Arrays;

public class WhiteHeuristics extends Heuristics{

    private final int WHITE_ALIVE = 0;
    private final int BLACK_EATEN = 1;
    private final int KING_MOVEMENT = 2;
    private final int SAFE_PAWNS = 3;

    // Flag to enable console print
    private boolean print = false;

    // Weights for evaluation in the following order: WhiteAlive, BlackEaten, BestTiles, NumKingEscapes
    private final Double[] gameWeights;

    public WhiteHeuristics(State state) {
        super(state);

        gameWeights = new Double[4];

        gameWeights[WHITE_ALIVE] = 35.0;
        gameWeights[BLACK_EATEN] = 18.0;
        gameWeights[KING_MOVEMENT] = 5.0;
        gameWeights[SAFE_PAWNS] = 42.0;

    }

    /**
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {
        double stateValue = 0.0;

        int[] kingPos = kingPosition(state);

        // if king can be captured PRUNE THOSE MFS
        if (canBeCaptured(state, kingPos, State.Pawn.KING))
            return Double.NEGATIVE_INFINITY;

        int numbOfBlack = state.getNumberOf(State.Pawn.BLACK);
        int numbOfWhite = state.getNumberOf(State.Pawn.WHITE);
        // Values for the weighted sum
        double numberOfWhiteAlive = (double)  numbOfWhite / GameAshtonTablut.NUM_WHITE;
        double numberOfBlackEaten = (double)  (GameAshtonTablut.NUM_BLACK - numbOfBlack) / GameAshtonTablut.NUM_BLACK;
        double safePawnsPercent = (double) getPawnsSafety() / numbOfWhite;
        double kingMovEval = evalKingMovement(kingPos);

        double evalKingEsc = evalKingEscapes(kingPos);

        stateValue += numberOfWhiteAlive * gameWeights[WHITE_ALIVE];
        stateValue += numberOfBlackEaten * gameWeights[BLACK_EATEN];
        stateValue += safePawnsPercent * gameWeights[SAFE_PAWNS];
        stateValue += kingMovEval * gameWeights[KING_MOVEMENT];

        stateValue += evalKingEsc;

        if (print) {
            System.out.println("White pawns alive: " + numberOfWhiteAlive);
            System.out.println("Number of black pawns eaten: " + numberOfBlackEaten);
            System.out.println("King mobility eval: " + kingMovEval);
            System.out.println("Eval king escapes: " + evalKingEsc);
            System.out.println("|GAME|: value is " + stateValue);
        }

        return stateValue;
    }

    private double evalKingMovement(int[] kPos) {
        int val = getKingMovement(state, kPos);

        if (val == 0)
            return 0.3;
        if (val == 1)
            return 1.0;

        return 1.2;
    }

    /**
     * @return the number of white pawns that can't be captured
     */
    private int getPawnsSafety() {
        int safe = 0;

        State.Pawn[][] board = state.getBoard();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equalsPawn(State.Pawn.WHITE.toString())) {
                    safe += canBeCaptured(state, new int[]{i, j}, State.Pawn.WHITE) ? 0 : 1;
                }
            }
        }

        return safe;
    }

    /**
     * @return a positive value for a SURE king escape
     */
    private double evalKingEscapes(int[] kPos) {
        int[] escapes = getKingEscapes(state, kPos);
        int numEsc = Arrays.stream(escapes).sum();
        if (numEsc > 1)
            return 200.0;
        // in case we have one escape only we check whether an enemy can block escape
        else if (numEsc == 1) {
            // up escape
            if (escapes[0] == 1) {
                for(int i = kPos[0]-1; i >= 0; i--) {
                    int[] checkPos = new int[]{i, kPos[1]};
                    if (checkLeftSide(state, State.Pawn.BLACK, checkPos) || checkRightSide(state, State.Pawn.BLACK, checkPos)) {
                        return 0.0;
                    }
                }
                return 80.0;
            }
            // down escape
            if (escapes[1] == 1) {
                for(int i = kPos[0]+1; i <= 8; i++) {
                    int[] checkPos = new int[]{i, kPos[1]};
                    if (checkLeftSide(state, State.Pawn.BLACK, checkPos) || checkRightSide(state, State.Pawn.BLACK, checkPos)) {
                        return 0.0;
                    }
                }
                return 80.0;
            }
            // left escape
            if (escapes[2] == 1) {
                for(int i = kPos[1]-1; i >= 0; i--) {
                    int[] checkPos = new int[]{kPos[0], i};
                    if (checkUpside(state, State.Pawn.BLACK, checkPos) || checkDownside(state, State.Pawn.BLACK, checkPos)) {
                        return 0.0;
                    }
                }
                return 80.0;
            }
            // right escape
            if (escapes[3] == 1) {
                for(int i = kPos[1]+1; i <= 8; i++) {
                    int[] checkPos = new int[]{kPos[0], i};
                    if (checkUpside(state, State.Pawn.BLACK, checkPos) || checkDownside(state, State.Pawn.BLACK, checkPos)) {
                        return 0.0;
                    }
                }
                return 80.0;
            }
        }
        return 0.0;
    }
}

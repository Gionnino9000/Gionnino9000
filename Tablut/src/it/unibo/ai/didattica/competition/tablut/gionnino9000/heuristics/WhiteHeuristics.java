package it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class WhiteHeuristics extends Heuristics{

    private final int WHITE_ALIVE = 0;
    private final int BLACK_EATEN = 1;
    private final int BLACK_SUR_K = 2;
    private final int N_KING_ESCAPES = 3;
    private final int KING_PROTECTION = 4;

    // ADD (?): King captured eval (LOSS), King escaped eval (WIN)

    // Flag to enable console print
    private boolean print = false;

    // Numb. tiles in rhombus
    private final int BEST_TILES_NUMB = 4;

    // Weights for evaluation in the following order: WhiteAlive, BlackEaten, BlackSurroundingKing, BestTiles, NumKingEscapes, KingProtection
    private final Double[] earlyGameWeights;
    private final Double[] lateGameWeights;

    public WhiteHeuristics(State state) {
        super(state);

        earlyGameWeights = new Double[5];
        earlyGameWeights[WHITE_ALIVE] = .0;
        earlyGameWeights[BLACK_EATEN] = .0;
        earlyGameWeights[BLACK_SUR_K] = .0;
        earlyGameWeights[N_KING_ESCAPES] = .0;
        earlyGameWeights[KING_PROTECTION] = .0;

        lateGameWeights = new Double[5];

    }

    /**
     * @return the evaluation of the states using a weighted sum
     */
    @Override
    public double evaluateState() {
        double stateValue = 0.0;
        boolean lateGame = false;

        int numbOfBlack = state.getNumberOf(State.Pawn.BLACK);
        /*
        if (numbOfBlack < 4)
            lateGame = true;
         */

        // Values for the weighted sum
        double numberOfWhiteAlive = (double)  state.getNumberOf(State.Pawn.WHITE) / GameAshtonTablut.NUM_WHITE;
        double numberOfBlackEaten = (double)  (GameAshtonTablut.NUM_BLACK - numbOfBlack) / GameAshtonTablut.NUM_BLACK;
        // brainmates tf ? => double blackSurroundKing = (double)(getNumEatenPositions(state) - checkNearPawns(state, kingPosition(state),State.Turn.BLACK.toString())) / getNumEatenPositions(state);
        double blackPawnsNearKing = (double) checkAdjacentPawns(state, kingPosition(state), State.Turn.BLACK.toString()) / getNumbToEatKing(state);
        double kingProtection = getKingProtection();

        double numKingEscapes = (double) countKingEscapes(state);
        if (numKingEscapes <= 1)
            numKingEscapes = 0.0;

        if (print) {
            System.out.println("White pawns alive: " + numberOfWhiteAlive);
            System.out.println("Number of black pawns near to the king:" + blackPawnsNearKing);
            System.out.println("Number of black pawns eaten: " + numberOfBlackEaten);
            System.out.println("Number of king escapes: " + numKingEscapes);
            System.out.println("King protection eval: " + kingProtection);
        }

        if (!lateGame) {
            stateValue += numberOfWhiteAlive * earlyGameWeights[WHITE_ALIVE];
            stateValue += numberOfBlackEaten * earlyGameWeights[BLACK_EATEN];
            stateValue += blackPawnsNearKing * earlyGameWeights[BLACK_SUR_K];
            stateValue += numKingEscapes * earlyGameWeights[N_KING_ESCAPES];
            stateValue += kingProtection * earlyGameWeights[KING_PROTECTION];

            if (print) {
                System.out.println("|EARLY_GAME|: value is " + stateValue);
            }
        } else {

            if (print) {
                System.out.println("|LATE_GAME|: value is " + stateValue);
            }
        }

        return stateValue;
    }

    private double getKingProtection() {
        // TO DO

        return 0.0;
    }

}

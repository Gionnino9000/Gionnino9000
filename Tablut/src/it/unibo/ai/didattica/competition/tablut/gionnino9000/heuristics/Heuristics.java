package it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.List;

public abstract class Heuristics {

    protected State state;

    public Heuristics(State state) {
        this.state = state;
    }

    public double evaluateState() {
        return 0;
    }

    /**
     * @return the position of the King
     */
    public int[] kingPosition(State state) {
        int[] pos = new int[2];

        State.Pawn[][] board = state.getBoard();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (state.getPawn(i, j).equalsPawn("K")) {
                    pos[0] = i;
                    pos[1] = j;

                    return pos;
                }
            }
        }
        return pos;
    }

    /**
     * @return true if king is on throne, false otherwise
     */
    public boolean isKingOnThrone(State state){
        return state.getPawn(4, 4).equalsPawn("K");
    }

    /**
     * @return the number of adjacent pawns that are target(BLACK, WHITE or King)
     */
    public int checkAdjacentPawns(State state, int[] pos, String target){
        int count = 0;

        State.Pawn[][] board = state.getBoard();
        if (board[pos[0]-1][pos[1]].equalsPawn(target))
            count++;
        if (board[pos[0]+1][pos[1]].equalsPawn(target))
            count++;
        if (board[pos[0]][pos[1]-1].equalsPawn(target))
            count++;
        if (board[pos[0]][pos[1]+1].equalsPawn(target))
            count++;
        return count;
    }

    /**
     * @return the adjacent positions occupied by target
     */
    protected List<int[]> adjacentPositionsOccupied(State state,int[] position, String target){
        List<int[]> occupiedPositions = new ArrayList<int[]>();
        int[] pos = new int[2];

        State.Pawn[][] board = state.getBoard();
        if (board[position[0]-1][position[1]].equalsPawn(target)) {
            pos[0] = position[0] - 1;
            pos[1] = position[1];
            occupiedPositions.add(pos);
        }
        if (board[position[0]+1][position[1]].equalsPawn(target)) {
            pos[0] = position[0] + 1;
            pos[1] = position[1];
            occupiedPositions.add(pos);
        }
        if (board[position[0]][position[1]-1].equalsPawn(target)) {
            pos[0] = position[0];
            pos[1] = position[1] - 1;
            occupiedPositions.add(pos);
        }
        if (board[position[0]][position[1]+1].equalsPawn(target)) {
            pos[0] = position[0];
            pos[1] = position[1] + 1;
            occupiedPositions.add(pos);
        }

        return occupiedPositions;
    }

    /**
     * @return true if king is adjacent
     */
    protected boolean checkAdjacentKing(State state, int[] position){
        return checkAdjacentPawns(state, position, "K") > 0;
    }


    /**
     * @return true if king is on an escape tile
     */
    public boolean hasKingEscaped(){
        int[] posKing = kingPosition(state);
        return posKing[0] == 0 || posKing[0] == 8 || posKing[1] == 0 || posKing[1] == 8;
    }


    /**
     * @return true if king is on a center tile
     */
    public boolean isKingOnCenter(State state,int[] kingPosition){
        return (kingPosition[0] > 2 && kingPosition[0] < 6 && kingPosition[1] > 2 && kingPosition[1] < 6);
    }

    /**
     * @return number of escapes which king can reach
     */
    public int countKingEscapes(State state){
        int[] kingPosition = this.kingPosition(state);
        int col = 0;
        int row = 0;

        if (!isKingOnCenter(state, kingPosition)) {
            if ((!(kingPosition[1] > 2 && kingPosition[1] < 6)) && (!(kingPosition[0] > 2 && kingPosition[0] < 6))) {
                col = countFreeColumn(state, kingPosition);
                row = countFreeRow(state,kingPosition);
            }
            if ((kingPosition[1] > 2 && kingPosition[1] < 6)) {
                row = countFreeRow(state, kingPosition);
            }
            if ((kingPosition[0] > 2 && kingPosition[0] < 6)) {
                col = countFreeColumn(state, kingPosition);
            }
            return (col + row);
        }

        return 0;
    }

    /**
     * @return number of free rows from given position
     */
    public int countFreeRow(State state, int[] position) {
        int row = position[0];
        int column = position[1];
        int[] currentPosition = new int[2];
        int freeWays = 2;

        currentPosition[0] = row;
        // left side
        for (int i = column-1; i >= 0; i--) {
            currentPosition[1] = i;
            if (isPositionOccupied(state,currentPosition)){
                freeWays--;
                break;
            }
        }

        // right side
        for (int i = column+1; i <= 8; i++) {
            currentPosition[1] = i;
            if (isPositionOccupied(state, currentPosition)) {
                freeWays--;
                break;
            }
        }

        return freeWays;
    }

    /**
     * @return number of free columns from given position
     */
    public int countFreeColumn(State state,int[] position){
        int row = position[0];
        int column = position[1];
        int[] currentPosition = new int[2];
        int freeWays = 2;

        currentPosition[1]=column;
        // downside
        for(int i=row+1;i<=8;i++) {
            currentPosition[0]=i;
            if (isPositionOccupied(state,currentPosition)) {
                freeWays--;
                break;
            }
        }

        // upside
        for(int i=row-1;i>=0;i--) {
            currentPosition[0]=i;
            if (isPositionOccupied(state,currentPosition)){
                freeWays--;
                break;
            }
        }

        return freeWays;
    }


    /**
     * @return true if a position is occupied, false otherwise
     */
    public boolean isPositionOccupied(State state,int[] position){
        return !state.getPawn(position[0], position[1]).equals(State.Pawn.EMPTY);
    }


    /**
     * @return number of positions needed to eat king in the current state
     */
    public int getNumbToEatKing(State state){
        int[] kingPosition = kingPosition(state);

        if (kingPosition[0] == 4 && kingPosition[1] == 4){
            return 4;
        } else if ((kingPosition[0] == 3 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 3)
                || (kingPosition[0] == 5 && kingPosition[1] == 4) || (kingPosition[0] == 4 && kingPosition[1] == 5)){
            return 3;
        } else{
            return 2;
        }
    }

}


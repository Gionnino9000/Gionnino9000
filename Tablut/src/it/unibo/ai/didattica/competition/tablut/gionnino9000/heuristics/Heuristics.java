package it.unibo.ai.didattica.competition.tablut.gionnino9000.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public abstract class Heuristics {

    protected State state;

    // Matrix of camps
    private final int[][] camps = {
                        {0,3}, {0,4}, {0,5},
                               {1,4},

            {3,0},                                 {3,8},
            {4,0}, {4,1},                   {4,7}, {4,8},
            {5,0},                                 {5,8},

                               {7,4},
                        {8,3}, {8,4}, {8,5},
    };

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
     * @return true if a camp is adjacent to pos
     */
    public boolean checkAdjacentCamp(int[] pos){
        if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, new int[]{pos[0]-1,pos[1]})))
            return true;
        if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, new int[]{pos[0]+1,pos[1]})))
            return true;
        if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, new int[]{pos[0],pos[1]-1})))
            return true;
        if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, new int[]{pos[0],pos[1]+1})))
            return true;
        return false;
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
        } else if (isKingOnCenter(state, kingPosition)) {
            return 3;
        } else if (checkAdjacentCamp(kingPosition)) {
            return 1;
        } else {
            return 2;
        }
    }

    public int getEnemyCount(State state, State.Pawn target, int quadrant) {
        int[] rowRange = new int[2];
        int[] columnRange = new int[2];
        int count = 0;

        if (quadrant == 1 || quadrant == 2) {
            rowRange[0] = 0;
            rowRange[1] = 3;
        } else {
            rowRange[0] = 5;
            rowRange[1] = 9;
        }

        if(quadrant == 1 || quadrant == 3) {
            columnRange[0] = 0;
            columnRange[1] = 3;
        } else {
            columnRange[0] = 5;
            columnRange[1] = 9;
        }

        for(int i = rowRange[0]; i <= rowRange[1]; i++) {
            for(int j = columnRange[0] ; j <= columnRange[1]; j++) {
                if(state.getBoard()[i][j].equalsPawn(target.toString()))
                    count++;
            }
        }

        return count;
    }

    /**
     * @param state the current state of the board
     * @param enemy color of the opposite pawn
     * @param position position
     *
     * @return return true or false whether an enemy pawn can go to that position from mentioned side
    */
    public boolean checkUpside(State state, State.Pawn enemy, int[] position) {
        for(int i = position[0]; i >= 0; i--) {
            if (isPositionOccupied(state, position)) {
                if (state.getBoard()[i][position[1]].equalsPawn(enemy.toString()))
                    return true;
                else return false;
            }
            if ((position[1] == 0 || position[1] == 8) && i == 3)
                return false;
            if ((position[1] == 1 || position[1] == 7) && i == 4)
                return false;

        }

        return false;
    }

    /**
     * @param state the current state of the board
     * @param enemy color of the opposite pawn
     * @param position position
     *
     * @return return true or false whether an enemy pawn can go to that position from mentioned side
     */
    public boolean checkDownside(State state, State.Pawn enemy, int[] position) {
        for(int i = position[0]; i <= 8; i++) {
            if (isPositionOccupied(state, position)) {
                if (state.getBoard()[i][position[1]].equalsPawn(enemy.toString()))
                    return true;
                else return false;
            }
            if ((position[1] == 0 || position[1] == 8) && i == 5)
                return false;
            if ((position[1] == 1 || position[1] == 7) && i == 4)
                return false;

        }

        return false;
    }

    /**
     * @param state the current state of the board
     * @param enemy color of the opposite pawn
     * @param position position
     *
     * @return return true or false whether an enemy pawn can go to that position from mentioned side
     */
    public boolean checkRightSide(State state, State.Pawn enemy, int[] position) {
        for(int i = position[1]; i <= 8; i++) {
            if (isPositionOccupied(state, position)) {
                if (state.getBoard()[position[0]][i].equalsPawn(enemy.toString()))
                    return true;
                else return false;
            }
            if ((position[0] == 0 || position[0] == 8) && i == 5)
                return false;
            if ((position[0] == 1 || position[0] == 7) && i == 4)
                return false;

        }

        return false;
    }

    /**
     * @param state the current state of the board
     * @param enemy color of the opposite pawn
     * @param position position
     *
     * @return return true or false whether an enemy pawn can go to that position from mentioned side
     */
    public boolean checkLeftSide(State state, State.Pawn enemy, int[] position) {
        for(int i = position[1]; i >= 0; i--) {
            if (isPositionOccupied(state, position)) {
                if (state.getBoard()[position[0]][i].equalsPawn(enemy.toString()))
                    return true;
                else return false;
            }
            if ((position[0] == 0 || position[0] == 8) && i == 3)
                return false;
            if ((position[0] == 1 || position[0] == 7) && i == 4)
                return false;

        }

        return false;
    }

    /**
     * @param state the current state of the board
     * @param position the position of the pawn
     *
     * @return return a true or false whether a pawn can be captured
    */

    public boolean canBeCaptured(State state, int[] position, State.Pawn pawn) {
        if ((position[0] == 0 || position[0] == 8) && (position[1] == 0 || position[1] == 8))
            return false;

        State.Pawn enemy = (pawn.equalsPawn(State.Pawn.WHITE.toString()) || pawn.equalsPawn(State.Pawn.KING.toString())) ? State.Pawn.BLACK : State.Pawn.WHITE;

        if (pawn.equalsPawn(State.Pawn.KING.toString()) && isKingOnCenter(state, position)) {
            int needed = getNumbToEatKing(state);

            if (checkAdjacentPawns(state, position, enemy.toString()) == (needed - 1)) {
                int[] space = new int[]{position[0]-1,position[1]};
                if (isPositionOccupied(state, space) && space[0] != 4 && space[1] != 4)
                    return horizzontalInvader(space, enemy);

                space[0] = position[0] + 1;
                if (isPositionOccupied(state, space) && space[0] != 4 && space[1] != 4)
                    return horizzontalInvader(space, enemy);

                space[0] = position[0];
                space[1] = position[1] - 1;
                if (isPositionOccupied(state, space) && space[0] != 4 && space[1] != 4)
                    return verticalInvader(space, enemy);

                space[1] = position[1] + 1;
                return verticalInvader(space, enemy);
            }
        }

        if (verticalInvader(position, enemy))
            return true;
        if (horizzontalInvader(position, enemy))
            return true;

        return false;
    }

    private boolean verticalInvader (int[] position, State.Pawn enemy) {
        int[] targetPos = new int[2];
        int[] checkPos = new int[2];

        // upside
        if (position[0] != 0) {
            targetPos[0] = position[0] - 1;
            targetPos[1] = position[1];

            if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, targetPos)) ||
                    (isPositionOccupied(state, targetPos) && state.getPawn(targetPos[0], targetPos[1]).equalsPawn(enemy.toString()))) {
                checkPos[0] = position[0] + 1;
                checkPos[1] = position[1];
                if (!isPositionOccupied(state, checkPos) && (checkLeftSide(state, enemy, checkPos) || checkRightSide(state, enemy, checkPos))) {
                    return true;
                }
            }
        }

        // downside
        if (position[0] != 8) {
            targetPos[0] = position[0] + 1;
            targetPos[1] = position[1];

            if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, targetPos)) ||
                    (isPositionOccupied(state, targetPos) && state.getPawn(targetPos[0], targetPos[1]).equalsPawn(enemy.toString()))) {
                checkPos[0] = position[0] - 1;
                checkPos[1] = position[1];
                if (!isPositionOccupied(state, checkPos) && (checkLeftSide(state, enemy, checkPos) || checkRightSide(state, enemy, checkPos))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean horizzontalInvader (int[] position, State.Pawn enemy) {
        int[] targetPos = new int[2];
        int[] checkPos = new int[2];

        // left side
        if (position[1] != 0) {
            targetPos[0] = position[0];
            targetPos[1] = position[1] - 1;

            if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, targetPos)) ||
                    (isPositionOccupied(state, targetPos) && state.getPawn(targetPos[0], targetPos[1]).equalsPawn(enemy.toString()))) {
                checkPos[0] = position[0];
                checkPos[1] = position[1] + 1;
                if (!isPositionOccupied(state, checkPos) && (checkUpside(state, enemy, checkPos) || checkDownside(state, enemy, checkPos))) {
                    return true;
                }
            }
        }

        // right side
        if (position[1] != 8) {
            targetPos[0] = position[0];
            targetPos[1] = position[1] + 1;

            if (Arrays.stream(camps).anyMatch(camp -> Arrays.equals(camp, targetPos)) ||
                    (isPositionOccupied(state, targetPos) && state.getPawn(targetPos[0], targetPos[1]).equalsPawn(enemy.toString()))) {
                checkPos[0] = position[0];
                checkPos[1] = position[1] - 1;
                if (!isPositionOccupied(state, checkPos) && (checkUpside(state, enemy, checkPos) || checkDownside(state, enemy, checkPos))) {
                    return true;
                }
            }
        }

        return false;
    }

}


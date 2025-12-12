package Piece;

import Board.Board;

public class Soldier extends Piece {
    public Soldier(int row, int col, boolean side) {
        super(row, col, side);
    }

    private boolean isAdvance(int targetRow, int targetCol) {
        if (targetCol != col)
            return false;

        if (side == RED && targetRow + 1 == row) return true;
        else if (side == BLACK && targetRow - 1 == row) return true;
        
        return false;
    }

    private boolean isTurn(int targetRow, int targetCol) {
        if (targetRow == row) 
            return Math.abs(targetCol - col) == 1;

        return false;
    }

    private boolean isCrossRiver() {
        if (side == RED) {
            return row <= 4;
        } else {
            return row >= 5;
        }
    }

    @Override
    String getLabel() {
        if (side == RED) return "兵";
        else return "卒";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        if (isCrossRiver()) {
            return isAdvance(targetRow, targetCol) || isTurn(targetRow, targetCol);
        } else {
            return isAdvance(targetRow, targetCol);
        }
    }
}

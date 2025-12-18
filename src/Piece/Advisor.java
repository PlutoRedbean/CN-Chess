package Piece;

import Board.Board;

public class Advisor extends Piece {
    public Advisor(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "仕";
        else return "士";
    }
    
    private boolean isDiagonal(int targetRow, int targetCol) {
        return Math.abs(targetRow - getRow()) == 1 && Math.abs(targetCol - getCol()) == 1;
    }

    private boolean isInPalace(int targetRow, int targetCol) {
        if (targetCol < 3 || targetCol > 5) return false;
        if (side == Piece.RED) {
            return targetRow >= 7 && targetRow <= 9;
        } else {
            return targetRow >= 0 && targetRow <= 2;
        }
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        return isDiagonal(targetRow, targetCol) && isInPalace(targetRow, targetCol);
    }
}

package Piece;

import Board.Board;

public class Horse extends Piece {
    public Horse(int row, int col, boolean side) {
        super(row, col, side);
    }

    private boolean isVertical(int targetRow, int targetCol) {
        if (Math.abs(targetCol - col) != 1 || Math.abs(targetRow - row) != 2)
            return false;
        else
            return true;
    }

    private boolean isHorizontal(int targetRow, int targetCol) {
        if (Math.abs(targetCol - col) != 2 || Math.abs(targetRow - row) != 1)
            return false;
        else
            return true;
    }

    private boolean isBlocked(int targetRow, int targetCol, Board board) {
        if (isVertical(targetRow, targetCol)) {
            return board.getPiece((row + targetRow) / 2, col) != null;
        } else if (isHorizontal(targetRow, targetCol)) {
            return board.getPiece(row, (col + targetCol) / 2) != null;
        }
        
        return true;
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "傌";
        else return "馬";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        return !isBlocked(targetRow, targetCol, board);
    }
}

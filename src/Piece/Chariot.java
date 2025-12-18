package Piece;

import Board.Board;

public class Chariot extends Piece {
    public Chariot(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "車";
        else return "俥";
    }private boolean isStraightLine(int targetRow, int targetCol) {
        return targetRow == row || targetCol == col;
    }

    private int countObstacles(int targetRow, int targetCol, Board board) {
        int count = 0;

        if (row == targetRow) {
            int min = Math.min(col, targetCol);
            int max = Math.max(col, targetCol);
            for (int c = min + 1; c < max; c++) {
                if (board.getPiece(row, c) != null) count++;
            }
        } else {
            int min = Math.min(row, targetRow);
            int max = Math.max(row, targetRow);
            for (int r = min + 1; r < max; r++) {
                if (board.getPiece(r, col) != null) count++;
            }
        }
        return count;
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        if (!isStraightLine(targetRow, targetCol)) return false;
        
        return countObstacles(targetRow, targetCol, board) == 0;
    }
}

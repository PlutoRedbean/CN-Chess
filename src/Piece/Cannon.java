package Piece;

import Board.Board;

public class Cannon extends Piece {
    public Cannon(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "炮";
        else return "砲";
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

        int obstacles = countObstacles(targetRow, targetCol, board);
        Piece target = board.getPiece(targetRow, targetCol);

        if (target == null) {
            return obstacles == 0;
        } else {
            return obstacles == 1;
        }
    }
}

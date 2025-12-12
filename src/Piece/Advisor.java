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

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        return true;
    }
}

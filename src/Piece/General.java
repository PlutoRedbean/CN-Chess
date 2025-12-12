package Piece;

import Board.Board;

public class General extends Piece {
    public General(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    protected String getLabel() {
        if (side == Piece.RED) return "帥";
        else return "將";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        return true;
    }
}

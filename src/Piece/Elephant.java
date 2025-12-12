package Piece;

import Board.Board;

public class Elephant extends Piece {
    public Elephant(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "相";
        else return "象";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        return true;
    }
}

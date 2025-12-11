package Piece;

public class Cannon extends Piece {
    public Cannon(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "炮";
        else return "砲";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Piece[][] board) {
        return true;
    }
}

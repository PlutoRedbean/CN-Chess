package Piece;

public class Soldier extends Piece {
    public Soldier(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "兵";
        else return "卒";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Piece[][] board) {
        return true;
    }
}

package Piece;

public class Horse extends Piece {
    public Horse(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "傌";
        else return "馬";
    }
}

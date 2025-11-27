package Piece;

public class Chariot extends Piece {
    public Chariot(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "車";
        else return "俥";
    }
}

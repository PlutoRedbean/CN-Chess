package Piece;

public class Cannon extends Piece {
    public Cannon(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "仕";
        else return "士";
    }
}

package Piece;

public class Solder extends Piece {
    public Solder(int row, int col, boolean side) {
        super(row, col, side);
    }

    @Override
    String getLabel() {
        if (side == Piece.RED) return "兵";
        else return "卒";
    }
}

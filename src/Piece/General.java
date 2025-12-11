package Piece;

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
    public boolean isValidMove(int targetRow, int targetCol, Piece[][] board) {
        return true;
    }
}

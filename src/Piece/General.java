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

    private boolean isOrthogonalOneStep(int targetRow, int targetCol) {
        return Math.abs(targetRow - getRow()) + Math.abs(targetCol - getCol()) == 1;
    }

    private boolean isInPalace(int targetRow, int targetCol) {
        if (targetCol < 3 || targetCol > 5) return false;
        if (side == Piece.RED) {
            return targetRow >= 7 && targetRow <= 9;
        } else {
            return targetRow >= 0 && targetRow <= 2;
        }
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        return isOrthogonalOneStep(targetRow, targetCol) && isInPalace(targetRow, targetCol);
    }
}

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

    private boolean isTianShape(int targetRow, int targetCol) {
        return Math.abs(targetRow - getRow()) == 2 && Math.abs(targetCol - getCol()) == 2;
    }
    
    private boolean isEyeBlocked(int targetRow, int targetCol, Board board) {
        int eyeRow = (getRow() + targetRow) / 2;
        int eyeCol = (getCol() + targetCol) / 2;
        return board.getPiece(eyeRow, eyeCol) != null;
    }

    // Check if the target position crosses the river
    private boolean isCrossRiver(int targetRow) {
        if (side == Piece.RED) {
            return targetRow < 5;
        } else {
            return targetRow > 4;
        }
    }
    
    @Override
    public boolean isValidMove(int targetRow, int targetCol, Board board) {
        if (!isTianShape(targetRow, targetCol)) return false;
        if (isCrossRiver(targetRow)) return false;
        return !isEyeBlocked(targetRow, targetCol, board);
    }
}

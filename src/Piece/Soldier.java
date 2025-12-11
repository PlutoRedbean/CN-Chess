package Piece;

public class Soldier extends Piece {
    public Soldier(int row, int col, boolean side) {
        super(row, col, side);
    }

    private boolean isAdvance(int targetRow, int targetCol) {
        if (targetCol != col) {
            return false;
        } else {
            if (side == RED && targetRow - 1) return true;
        }
    }

    private boolean isCrossRiver() {
        if (side == RED) {
            return row <= 4;
        } else {
            return row >= 5;
        }
    }

    @Override
    String getLabel() {
        if (side == RED) return "兵";
        else return "卒";
    }

    @Override
    public boolean isValidMove(int targetRow, int targetCol, Piece[][] board) {
        if (isCrossRiver()) {
            if (targetRow == row) {
                return Math.abs(targetCol - col) == 1;
            } else if (targetCol == col) {
                return targetRow - row == (side == RED ? -1 : 1);
            } else {
                return false;
            }
        } else {
            return targetCol == col && targetRow - row == (side == RED ? -1 : 1);
        }
    }
}

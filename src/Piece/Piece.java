package Piece;
import java.awt.*;

import Board.Board;

public abstract class Piece {
    private int row;
    private int col;
    boolean side; // true = RED, false = BLACK

    public static final boolean RED = true;
    public static final boolean BLACK = false;
    public static final java.awt.Color COLOR_RED = new Color(180, 0, 0);
    public static final java.awt.Color COLOR_BLACK = Color.BLACK;

    public Piece(int row, int col, boolean side) {
        if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS) {
            throw new IllegalArgumentException("row/col 越界");
        }
        this.row = row;
        this.col = col;
        // this.label = label;
        this.side = side;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public boolean getSide() { return side; }

    abstract String getLabel();

    private void paint_border(Graphics2D g2, int centerX, int centerY, int size) {
        int borderSize = (int)Math.round(size * 0.85);

        int x = centerX - size / 2;
        int y = centerY - size / 2;

        g2.setColor(new Color(255, 200, 120));
        g2.fillOval(x, y, size, size);

        g2.setColor(Color.DARK_GRAY);
        g2.drawOval(x, y, size, size);

        int borderX = centerX - borderSize / 2;
        int borderY = centerY - borderSize / 2;
        if (side == RED) {
            g2.setColor(COLOR_RED);
            g2.drawOval(borderX, borderY, borderSize, borderSize);
        } else {
            g2.setColor(COLOR_BLACK);
            g2.drawOval(borderX, borderY, borderSize, borderSize);
        }
    }

    private void paint_label(Graphics2D g2, int centerX, int centerY, int size) {
        String label = getLabel();
        int fontSize = Math.max(12, size / 2);
        Font font = new Font("Serif", Font.BOLD, fontSize);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int tx = centerX - fm.stringWidth(label) / 2;
        int ty = centerY + (fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(side == RED ? COLOR_RED : COLOR_BLACK);
        g2.drawString(label, tx, ty);
    }
    
    public void drawAt(Graphics g, int centerX, int centerY, int size) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        paint_border(g2, centerX, centerY, size);

        paint_label(g2, centerX, centerY, size);
    }

    public void setPos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract boolean isValidMove(int targetRow, int targetCol, Piece[][] board);
}

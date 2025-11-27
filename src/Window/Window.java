package Window;
import java.awt.*;
import java.awt.event.*;

import Board.Board;
import Piece.Piece;

public class Window extends Frame {
    private int width;
    private int height;
    private Board board;

    public Window(int width, int height) {
        this.width = width;
        this.height = height;
        window_init();
        window_update();
    }

    public Window() {
        this(500, 600);
    }

    private void window_init() {
        setTitle("中国象棋");
        setSize(width, height);

        board = new Board();
        add(board, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void window_update() {
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                width = getWidth();
                height = getHeight();
                board.revalidate();
                board.repaint();
            }
        });
    }

    /**
     * 把 Piece 对象放到它指定的 row,col
     * Piece 必须包含 row/col（0..9, 0..8）
     */
    public void add_piece(Piece p) {
        if (p == null) return;
        board.setPiece(p.getRow(), p.getCol(), p);
    }

    public void remove_piece(int row, int col) {
        board.setPiece(row, col, null);
    }
}

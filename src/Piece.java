import java.awt.*;

/* 棋子类（保持 row/col/label/side 字段） */
class Piece {
    private int row;
    private int col;
    private String label;
    private boolean side; // true = RED, false = BLACK

    public Piece(int row, int col, String label, boolean side) {
        if (row < 0 || row >= Board.ROWS || col < 0 || col >= Board.COLS) {
            throw new IllegalArgumentException("row/col 越界");
        }
        this.row = row;
        this.col = col;
        this.label = label;
        this.side = side;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public String getLabel() { return label; }
    public boolean getSide() { return side; }

    /**
     * 在给定的交叉点中心 (centerX, centerY) 和指定直径 size 内绘制棋子
     */
    public void drawAt(Graphics g, int centerX, int centerY, int size) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int x = centerX - size / 2;
        int y = centerY - size / 2;

        // 外圈填充
        g2.setColor(new Color(255, 200, 120));
        g2.fillOval(x, y, size, size);

        // 边框
        g2.setColor(Color.DARK_GRAY);
        g2.drawOval(x, y, size, size);

        // 文本（根据 size 自动调整字体）
        int fontSize = Math.max(12, size / 2);
        Font font = new Font("Serif", Font.BOLD, fontSize);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int tx = centerX - fm.stringWidth(label) / 2;
        int ty = centerY + (fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(side == Const.RED ? new Color(180, 0, 0) : Color.BLACK);
        g2.drawString(label, tx, ty);
    }
}

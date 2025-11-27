import java.awt.*;

/* 棋盘：绘制线条、楚河汉界，并在交叉点绘制棋子 */
class Board extends Canvas {
    public static final int ROWS = 10; // 10 horizontal lines -> 10 intersection rows
    public static final int COLS = 9;  // 9 vertical lines -> 9 intersection cols

    private Piece[][] pieces = new Piece[ROWS][COLS];

    public Board() {
        // 让父窗口用 BorderLayout 把这个 Canvas 扩展到中央
        setPreferredSize(new Dimension(600, 700));
    }

    /**
     * 设置某个交叉点上的棋子（row:0..9, col:0..8）
     */
    public void setPiece(int row, int col, Piece piece) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            throw new IllegalArgumentException("row/col 越界: " + row + "," + col);
        }
        pieces[row][col] = piece;
        repaint();
    }

    public Piece getPiece(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) return null;
        return pieces[row][col];
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int H = getHeight();
        int W = getWidth();

        H = H > W ? H : W;
        W = H > W ? H : W;

        // 外围留白（相对）
        int margin = Math.min(W, H) / 20; // 动态留白，防止棋子贴边
        double usableW = W - 2.0 * margin;
        double usableH = H - 2.0 * margin;

        // 计算每两个交叉点之间的像素距离（COLS-1 间隔, ROWS-1 间隔）
        double cellW = usableW / (COLS - 1);
        double cellH = usableH / (ROWS - 1);

        // 背景
        g2.setColor(new Color(245, 222, 179)); // 浅木色
        g2.fillRect(0, 0, W, H);

        // 绘制横线（10 条）
        g2.setColor(Color.DARK_GRAY);
        for (int r = 0; r < ROWS; r++) {
            int y = (int) Math.round(margin + r * cellH);
            g2.drawLine(margin, y, W - margin, y);
        }

        // 绘制竖线（9 条）
        for (int c = 0; c < COLS; c++) {
            int x = (int) Math.round(margin + c * cellW);
            g2.drawLine(x, margin, x, H - margin);
        }

        // 可选：在交叉点处画小点以示位置（不必要）
        // for (int r = 0; r < ROWS; r++) {
        //     for (int c = 0; c < COLS; c++) {
        //         int x = (int)Math.round(margin + c * cellW);
        //         int y = (int)Math.round(margin + r * cellH);
        //         g2.fillOval(x-2, y-2, 4, 4);
        //     }
        // }

        // 绘制“楚河” / “汉界” —— 在第4行和第5行中间
        double yRiver = margin + (4 + 0.5) * cellH;
        Font riverFont = new Font("Serif", Font.BOLD, Math.max(12, (int)(cellH * 0.6)));
        g2.setFont(riverFont);
        FontMetrics fm = g2.getFontMetrics();
        String left = "楚河";
        String right = "汉界";
        // 左侧 "楚河" 放在中线左偏，右侧 "汉界" 放在中线右偏
        int cx = W / 2;
        int lx = cx - 40 - fm.stringWidth(left); // 根据需要微调偏移
        int rx = cx + 40;
        int ty = (int) Math.round(yRiver + (fm.getAscent() - fm.getDescent()) / 2.0);
        g2.setColor(new Color(120, 60, 20)); // 深棕色文字
        g2.drawString(left, lx, ty);
        g2.drawString(right, rx, ty);

        // 绘制棋子：每个 piece 在 (margin + c*cellW, margin + r*cellH) 的交叉点为中心
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Piece p = pieces[r][c];
                if (p != null) {
                    int centerX = (int) Math.round(margin + c * cellW);
                    int centerY = (int) Math.round(margin + r * cellH);
                    // 棋子直径取交叉间距的 0.8 倍，避免粘到线
                    int pieceSize = (int) Math.round(Math.min(cellW, cellH) * 0.8);
                    p.drawAt(g2, centerX, centerY, pieceSize);
                }
            }
        }
    }
}


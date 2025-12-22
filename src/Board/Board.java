package Board;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

import Common.Cmd;
import Network.NetworkClient;
import Piece.Piece;
import Piece.Soldier;
import Piece.Advisor;
import Piece.Cannon;
import Piece.Chariot;
import Piece.Elephant;
import Piece.General;
import Piece.Horse;

public class Board extends JPanel {
    public static final int ROWS = 10;
    public static final int COLS = 9;

    private Piece[][] pieces = new Piece[ROWS][COLS];

    private Piece selectedPiece = null;
    private boolean isRedTurn = Piece.RED;

    private double cellW, cellH;
    private int borderLeft, borderTop;

    private NetworkClient netClient;
    private boolean mySide;
    private boolean isOnline = false;

    private GameListener gameListener;
    private boolean isGameOver = false;

    public Board() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    public void initGame(boolean isRed) {
        this.mySide = isRed;
        this.isOnline = true;
        reset();
        repaint();
    }

    public void reset() {
        // 清空棋子数组
        for(int r=0; r<ROWS; r++) {
            for(int c=0; c<COLS; c++) {
                pieces[r][c] = null;
            }
        }
        selectedPiece = null;
        isGameOver = false;
        isRedTurn = true; // 红方先行
        
        initPiecesStandard();
        repaint();
    }

    private void initPiecesStandard() {
        // 黑方
        setPiece(0, 0, new Chariot(0, 0, Piece.BLACK));
        setPiece(0, 1, new Horse(0, 1, Piece.BLACK));
        setPiece(0, 2, new Elephant(0, 2, Piece.BLACK));
        setPiece(0, 3, new Advisor(0, 3, Piece.BLACK));
        setPiece(0, 4, new General(0, 4, Piece.BLACK));
        setPiece(0, 5, new Advisor(0, 5, Piece.BLACK));
        setPiece(0, 6, new Elephant(0, 6, Piece.BLACK));
        setPiece(0, 7, new Horse(0, 7, Piece.BLACK));
        setPiece(0, 8, new Chariot(0, 8, Piece.BLACK));
        setPiece(2, 1, new Cannon(2, 1, Piece.BLACK));
        setPiece(2, 7, new Cannon(2, 7, Piece.BLACK));
        for(int i=0; i<=8; i+=2) setPiece(3, i, new Soldier(3, i, Piece.BLACK));

        // 红方
        setPiece(9, 0, new Chariot(9, 0, Piece.RED));
        setPiece(9, 1, new Horse(9, 1, Piece.RED));
        setPiece(9, 2, new Elephant(9, 2, Piece.RED));
        setPiece(9, 3, new Advisor(9, 3, Piece.RED));
        setPiece(9, 4, new General(9, 4, Piece.RED));
        setPiece(9, 5, new Advisor(9, 5, Piece.RED));
        setPiece(9, 6, new Elephant(9, 6, Piece.RED));
        setPiece(9, 7, new Horse(9, 7, Piece.RED));
        setPiece(9, 8, new Chariot(9, 8, Piece.RED));
        setPiece(7, 1, new Cannon(7, 1, Piece.RED));
        setPiece(7, 7, new Cannon(7, 7, Piece.RED));
        for(int i=0; i<=8; i+=2) setPiece(6, i, new Soldier(6, i, Piece.RED));
    }
    
    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }

    private void handleMouseClick(int x, int y) {

        if (isGameOver) return; // 游戏结束后不处理点击

        if (isOnline && isRedTurn != mySide) return;
        
        // 四舍五入判断点击了哪个交叉点范围
        int col = (int) Math.round((x - borderLeft) / cellW);
        int row = (int) Math.round((y - borderTop) / cellH);

        if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
            return;
        }

        Piece clickedPiece = pieces[row][col];

        if (selectedPiece == null) {
            if (clickedPiece != null && clickedPiece.getSide() == isRedTurn) {
                selectedPiece = clickedPiece;
                repaint();
            }
        } else {
            
            // 点击了同色棋子 -> 更改选择
            if (clickedPiece != null && clickedPiece.getSide() == isRedTurn) {
                selectedPiece = clickedPiece;
                repaint();
            } else if (selectedPiece.isValidMove(row, col, this)) {
                boolean isWinMove = false;
                if (clickedPiece instanceof General) {
                    isWinMove = true;
                }

                if (isWinMove || isMoveSafe(selectedPiece, row, col)) {
                    if (isOnline) {
                        netClient.sendMove(selectedPiece.getRow(), selectedPiece.getCol(), row, col);
                        
                        boolean isWin = (clickedPiece instanceof General);
                        movePiece(selectedPiece, row, col);
                        
                        if (isWin) {
                            String winner = mySide ? Cmd.RED_WIN : Cmd.BLACK_WIN;
                            netClient.sendWin(winner);
                        }
                    } else {
                        movePiece(selectedPiece, row, col);
                    }
                    
                    if (isWinMove) {
                        isGameOver = true;
                        if (gameListener != null) {
                            // 谁的回合谁获胜（因为是当前行动方吃掉了对方）
                            // 注意：movePiece 内部已经切换了 isRedTurn，所以这里赢家是 !isRedTurn
                            gameListener.onGameOver(!isRedTurn);
                        }
                    }
                }
            }
        }
    }

    public void setNetworkClient(NetworkClient nc) { this.netClient = nc; }

    public void netMovePiece(int r1, int c1, int r2, int c2) {
        Piece p = pieces[r1][c1];
        if (p != null) {
            movePiece(p, r2, c2); 
        }
    }
    
    private boolean isOpposingGeneral() {
        Piece redGeneral = null;
        Piece blackGeneral = null;

        for (int c = 3; c <= 5; c++) {
            redGeneral = null;
            blackGeneral = null;
            
            for (int r = 0; r < ROWS; r++) {
                Piece p = pieces[r][c];
                if (p != null) {
                    if (p instanceof General) {
                        if (p.getSide() == Piece.RED) redGeneral = p;
                        else blackGeneral = p;
                    }
                }
            }

            if (redGeneral != null && blackGeneral != null) {
                int r1 = redGeneral.getRow();
                int r2 = blackGeneral.getRow();
                int min = Math.min(r1, r2);
                int max = Math.max(r1, r2);

                boolean blocked = false;
                for (int r = min + 1; r < max; r++) {
                    if (pieces[r][c] != null) {
                        blocked = true;
                        break;
                    }
                }

                if (!blocked) return true;
            }
        }
        return false;
    }

    private boolean isMoveSafe(Piece piece, int targetRow, int targetCol) {
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        Piece targetPiece = pieces[targetRow][targetCol];

        pieces[oldRow][oldCol] = null;
        pieces[targetRow][targetCol] = piece;
        piece.setPos(targetRow, targetCol);

        boolean isSafe = !isOpposingGeneral();

        piece.setPos(oldRow, oldCol);               // 恢复棋子坐标
        pieces[oldRow][oldCol] = piece;             // 恢复原位置棋子
        pieces[targetRow][targetCol] = targetPiece; // 恢复目标位置棋子（被吃的吐出来）

        return isSafe;
    }

    private void movePiece(Piece p, int targetRow, int targetCol) {
        pieces[p.getRow()][p.getCol()] = null;
        
        pieces[targetRow][targetCol] = p;

        p.setPos(targetRow, targetCol);

        isRedTurn = !isRedTurn;
        selectedPiece = null;

        repaint();
    }

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

    private void paint_background(Graphics2D g2, int winW, int winH,
                                  int boardX, int boardY, int boardSide) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, winW, winH);
        g2.setColor(new Color(245, 222, 179));
        g2.fillRect(boardX, boardY, boardSide, boardSide);
    }

    private void paint_line(Graphics2D g2, int borderTop, int borderBottom,
                            int borderLeft, int borderRight, double cellH, double cellW) {
        g2.setColor(Color.DARK_GRAY);

        int x1, x2, y1_upper, y2_upper, y1_lower, y2_lower;
        
        for (int r = 0; r < ROWS; r++) {
            int y = (int) Math.round(borderTop + r * cellH);
            x1 = borderLeft;
            x2 = borderRight;
            g2.drawLine(x1, y, x2, y);
        }

        for (int c = 0; c < COLS; c++) {
            int x = (int) Math.round(borderLeft + c * cellW);
            y1_upper = borderTop;
            y2_upper = (int) Math.round(borderTop + 4 * cellH);
            y1_lower = (int) Math.round(borderBottom - 4 * cellH);
            y2_lower = borderBottom;
            if (c == 0 || c == COLS - 1) {
                g2.drawLine(x, y1_upper, x, y2_lower);
            }
            g2.drawLine(x, y1_upper, x, y2_upper);
            g2.drawLine(x, y1_lower, x, y2_lower);
        }

        x1 = (int) Math.round(borderLeft + cellW * 3);
        x2 = (int) Math.round(borderLeft + cellW * 5);
        y1_upper = borderTop;
        y2_upper = (int) Math.round(borderTop + 2 * cellH);
        y1_lower = (int) Math.round(borderBottom - 2 * cellH);
        y2_lower = borderBottom;
        g2.drawLine(x1, y1_upper, x2, y2_upper);
        g2.drawLine(x2, y1_upper, x1, y2_upper);
        g2.drawLine(x1, y1_lower, x2, y2_lower);
        g2.drawLine(x2, y1_lower, x1, y2_lower);
    }

    private void paint_river(Graphics2D g2, int borderTop, int borderBottom,
                            int borderLeft, int borderRight, double cellH, double cellW) {
        double yRiver = borderTop + (4 + 0.5) * cellH;
        int fontSize = Math.max(12, (int)(cellH * 0.6));
        int offset = fontSize;

        Font riverFont = new Font("Serif", Font.BOLD, fontSize);
        g2.setFont(riverFont);
        FontMetrics fm = g2.getFontMetrics();
        String left = "楚河";
        String right = "汉界";
        
        int cx = (borderLeft + borderRight) / 2;
        int lx = cx - offset - fm.stringWidth(left);
        int rx = cx + offset;
        int ty = (int) Math.round(yRiver + (fm.getAscent() - fm.getDescent()) / 2.0);

        g2.setColor(new Color(120, 60, 20)); // 深棕色
        g2.drawString(left, lx, ty);
        g2.drawString(right, rx, ty);
    }

    private void paint_pieces(Graphics2D g2, int borderTop, int borderBottom,
                              int borderLeft, int borderRight, double cellH, double cellW) {
        // 绘制棋子：每个 piece 在 (padding + c*cellW, padding + r*cellH) 的交叉点为中心
        double sizeK = 0.8;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Piece p = pieces[r][c];
                if (p != null) {
                    int centerX = (int) Math.round(borderLeft + c * cellW);
                    int centerY = (int) Math.round(borderTop + r * cellH);
                    int pieceSize = (int) Math.round(Math.min(cellW, cellH) * sizeK);
                    p.drawAt(g2, centerX, centerY, pieceSize);

                    // 绘制选中框
                    if (p == selectedPiece) {
                        g2.setColor(Color.BLUE);
                        g2.setStroke(new BasicStroke(3)); // 加粗线条
                        int s = (int)(pieceSize * 1.1); // 稍微大一点的框
                        g2.drawRect(centerX - s/2, centerY - s/2, s, s);
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int winW = getWidth();
        int winH = getHeight();

        // 计算布局并保存到成员变量，供鼠标事件使用
        int boardSide = Math.min(winW, winH);
        int boardX = (winW - boardSide) / 2;
        int boardY = (winH - boardSide) / 2;
        
        int padding = Math.max(4, boardSide / 20);
        double usableW = boardSide - 2.0 * padding;
        double usableH = boardSide - 2.0 * padding;

        this.cellW = usableW / (COLS - 1);
        this.cellH = usableH / (ROWS - 1);
        this.borderLeft = boardX + padding;
        int borderRight = boardX + boardSide - padding;
        this.borderTop = boardY + padding;
        int borderBottom = boardY + boardSide - padding;

        paint_background(g2, winW, winH, boardX, boardY, boardSide);

        paint_line(g2, borderTop, borderBottom, borderLeft, borderRight, cellH, cellW);

        paint_river(g2, borderTop, borderBottom, borderLeft, borderRight, cellH, cellW);
        
        paint_pieces(g2, borderTop, borderBottom, borderLeft, borderRight, cellH, cellW);
        
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 800);
    }
}


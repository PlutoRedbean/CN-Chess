import java.awt.*;

class Piece extends Element {
    public Piece(int x, int y, int width, int height) {
        super(x, y, width, height);
    }
    
    @Override
    public void paint(Graphics g) {
        paint_init(g);
        
        g.setColor(Color.YELLOW);
        g.fillOval(x, y, width, height);
    }
}

import java.awt.*;

class Element extends Canvas {
    int x;
    int y;
    int width;
    int height;
    /*Consider a canvas as a square. x and y representing top left spot.
     *
     *  (x,y)
     *    +-----+
     *    |     |
     *    |     |
     *    +-----+
     * 
     */
    Element() {
        x = 0;
        y = 0;
        width = 10;
        height = 10;
    }

    Element(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    void paint_init(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
    }
}

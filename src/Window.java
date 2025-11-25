import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

class Window extends Frame {
    private int width;
    private int height;
    private ArrayList<Piece> pieces = new ArrayList<Piece>();
    
    public Window(int width, int height) {
        this.width = width;
        this.height = height;
        window_init();
        window_update();
    }
    
    public Window() {
        this.width = 400; 
        this.height = 400;
        window_init();
        window_update();
    }

    private void window_init() {
        setSize(width, height);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void window_update() {
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                width = getWidth();
                height = getHeight();
            }
        });
    }

    public void add_piece(Piece c) {
        pieces.add(c);
        add(c);
    }
}

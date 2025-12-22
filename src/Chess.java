import javax.swing.SwingUtilities;

import Window.Window;

public class Chess {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            try {
                new Window(1000, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

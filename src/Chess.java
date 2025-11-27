import Piece.Advisor;
import Piece.General;
import Piece.Piece;
import Piece.Solder;
import Window.Window;

public class Chess {
    public static void main(String[] args) throws Exception {
        Window window = new Window(1000, 1000);
        Piece p1 = new Solder(6, 4, Piece.BLACK);
        window.add_piece(p1);
        Piece p2 = new Solder(1, 1, Piece.RED);
        window.add_piece(p2);
        Piece p3 = new General(0, 4, Piece.BLACK);
        window.add_piece(p3);
        Piece p4 = new General(9, 4, Piece.RED);
        window.add_piece(p4);
        Piece p5 = new Advisor(1, 4, Piece.RED);
        window.add_piece(p5);
        window.remove_piece(1, 1);
    }
}

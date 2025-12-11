import Piece.Advisor;
import Piece.Cannon;
import Piece.Chariot;
import Piece.Elephant;
import Piece.General;
import Piece.Horse;
import Piece.Piece;
import Piece.Soldier;
import Window.Window;

public class Chess {
    public static void main(String[] args) throws Exception {
        Window window = new Window(1000, 1000);
        pieces_init(window);
    }

    private static void pieces_init(Window window) {
        Piece black_chariot0 = new Chariot(0, 0, Piece.BLACK);
        window.add_piece(black_chariot0);
        Piece black_horse0 = new Horse(0, 1, Piece.BLACK);
        window.add_piece(black_horse0);
        Piece black_elephant0 = new Elephant(0, 2, Piece.BLACK);
        window.add_piece(black_elephant0);
        Piece black_advisor0 = new Advisor(0, 3, Piece.BLACK);
        window.add_piece(black_advisor0);
        Piece black_general = new General(0, 4, Piece.BLACK);
        window.add_piece(black_general);
        Piece black_advisor1 = new Advisor(0, 5, Piece.BLACK);
        window.add_piece(black_advisor1);
        Piece black_elephant1 = new Elephant(0, 6, Piece.BLACK);
        window.add_piece(black_elephant1);
        Piece black_horse1 = new Horse(0, 7, Piece.BLACK);
        window.add_piece(black_horse1);
        Piece black_chariot1 = new Chariot(0, 8, Piece.BLACK);
        window.add_piece(black_chariot1);

        Piece black_cannon0 = new Cannon(2, 1, Piece.BLACK);
        window.add_piece(black_cannon0);
        Piece black_cannon1 = new Cannon(2, 7, Piece.BLACK);
        window.add_piece(black_cannon1);

        Piece black_soldier0 = new Soldier(3, 0, Piece.BLACK);
        window.add_piece(black_soldier0);
        Piece black_soldier1 = new Soldier(3, 2, Piece.BLACK);
        window.add_piece(black_soldier1);
        Piece black_soldier2 = new Soldier(3, 4, Piece.BLACK);
        window.add_piece(black_soldier2);
        Piece black_soldier3 = new Soldier(3, 6, Piece.BLACK);
        window.add_piece(black_soldier3);
        Piece black_soldier4 = new Soldier(3, 8, Piece.BLACK);
        window.add_piece(black_soldier4);
        
        Piece red_chariot0 = new Chariot(9, 0, Piece.RED);
        window.add_piece(red_chariot0);
        Piece red_horse0 = new Horse(9, 1, Piece.RED);
        window.add_piece(red_horse0);
        Piece red_elephant0 = new Elephant(9, 2, Piece.RED);
        window.add_piece(red_elephant0);
        Piece red_advisor0 = new Advisor(9, 3, Piece.RED);
        window.add_piece(red_advisor0);
        Piece red_general = new General(9, 4, Piece.RED);
        window.add_piece(red_general);
        Piece red_advisor1 = new Advisor(9, 5, Piece.RED);
        window.add_piece(red_advisor1);
        Piece red_elephant1 = new Elephant(9, 6, Piece.RED);
        window.add_piece(red_elephant1);
        Piece red_horse1 = new Horse(9, 7, Piece.RED);
        window.add_piece(red_horse1);
        Piece red_chariot1 = new Chariot(9, 8, Piece.RED);
        window.add_piece(red_chariot1);

        Piece red_cannon0 = new Cannon(7, 1, Piece.RED);
        window.add_piece(red_cannon0);
        Piece red_cannon1 = new Cannon(7, 7, Piece.RED);
        window.add_piece(red_cannon1);

        Piece red_soldier0 = new Soldier(6, 0, Piece.RED);
        window.add_piece(red_soldier0);
        Piece red_soldier1 = new Soldier(6, 2, Piece.RED);
        window.add_piece(red_soldier1);
        Piece red_soldier2 = new Soldier(6, 4, Piece.RED);
        window.add_piece(red_soldier2);
        Piece red_soldier3 = new Soldier(6, 6, Piece.RED);
        window.add_piece(red_soldier3);
        Piece red_soldier4 = new Soldier(6, 8, Piece.RED);
        window.add_piece(red_soldier4);
    }
}

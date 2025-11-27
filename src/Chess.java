import Piece.Advisor;
import Piece.Cannon;
import Piece.Chariot;
import Piece.Elephant;
import Piece.General;
import Piece.Horse;
import Piece.Piece;
import Piece.Solder;
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

        Piece black_solder0 = new Solder(3, 0, Piece.BLACK);
        window.add_piece(black_solder0);
        Piece black_solder1 = new Solder(3, 2, Piece.BLACK);
        window.add_piece(black_solder1);
        Piece black_solder2 = new Solder(3, 4, Piece.BLACK);
        window.add_piece(black_solder2);
        Piece black_solder3 = new Solder(3, 6, Piece.BLACK);
        window.add_piece(black_solder3);
        Piece black_solder4 = new Solder(3, 8, Piece.BLACK);
        window.add_piece(black_solder4);
        
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

        Piece red_solder0 = new Solder(6, 0, Piece.RED);
        window.add_piece(red_solder0);
        Piece red_solder1 = new Solder(6, 2, Piece.RED);
        window.add_piece(red_solder1);
        Piece red_solder2 = new Solder(6, 4, Piece.RED);
        window.add_piece(red_solder2);
        Piece red_solder3 = new Solder(6, 6, Piece.RED);
        window.add_piece(red_solder3);
        Piece red_solder4 = new Solder(6, 8, Piece.RED);
        window.add_piece(red_solder4);
    }
}

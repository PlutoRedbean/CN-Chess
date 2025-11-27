public class Chess {
    public static void main(String[] args) throws Exception {
        Window window = new Window(500, 500);
        Piece p1 = new Piece(6, 4, "兵", Piece.BLACK);
        window.add_piece(p1);
        Piece p2 = new Piece(1, 1, "帅", Piece.RED);
        window.add_piece(p2);
    }
}
